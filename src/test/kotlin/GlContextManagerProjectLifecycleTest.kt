import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.util.ref.GCUtil
import glsl.plugin.preview.GlContextManager
import java.lang.ref.WeakReference

/**
 * Each project has its own [GlContextManager], which owns that project's shader preview canvas and GL context.
 * These tests check that the manager lives and dies with its project: a closed project's manager must be
 * disposed, and nothing in the preview may keep the closed project reachable.
 */
class GlContextManagerProjectLifecycleTest : BasePlatformTestCase() {

    fun testEachProjectGetsItsOwnManager() {
        withTemporaryProject("otherProject") { otherProject ->
            val otherManager = GlContextManager.getInstance(otherProject)

            assertSame(
                "expected the same project to always get the same manager",
                otherManager,
                GlContextManager.getInstance(otherProject)
            )
            assertNotSame(
                "expected two projects to get two different managers",
                otherManager,
                GlContextManager.getInstance(project)
            )
        }
    }

    fun testManagerIsDisposedWhenItsProjectIsClosed() {
        // The platform disposes a Disposable's children when it disposes the Disposable itself, so this flag
        // flips exactly when the manager is disposed through the platform.
        var managerDisposed = false
        withTemporaryProject("closedProject") { closedProject ->
            val manager = GlContextManager.getInstance(closedProject)
            Disposer.register(manager) { managerDisposed = true }
            assertFalse("expected the manager not to be disposed while its project is open", managerDisposed)
        }

        assertTrue(
            "expected closing a project to dispose its manager (and with it the canvas and GL context)",
            managerDisposed
        )
    }

    fun testClosedProjectCanBeGarbageCollected() {
        // Only hold the project weakly, so this test itself doesn't keep it reachable.
        var closedProjectRef = WeakReference<Project>(null)
        withTemporaryProject("closedProject") { closedProject ->
            closedProjectRef = WeakReference(closedProject)
            GlContextManager.getInstance(closedProject)
        }

        // Keeps allocating until even soft references are cleared, so weakly reachable objects are gone too.
        GCUtil.tryGcSoftlyReachableObjects()

        assertNull(
            "expected a closed project to be garbage-collectable, but something (e.g. a static cache) still references it",
            closedProjectRef.get()
        )
    }
}
