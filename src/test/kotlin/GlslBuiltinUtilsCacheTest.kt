import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import glsl.plugin.utils.GlslBuiltinUtils

/**
 * GlslBuiltinUtils's builtin-element caches are scoped per-project. Verifies that disposing the
 * project a cache was built against doesn't leak stale elements into an unrelated project.
 */
class GlslBuiltinUtilsCacheTest : BasePlatformTestCase() {

    fun testBuiltinConstantsAreScopedPerProject() {
        assertCacheIsScopedPerProject("builtin constants") { project ->
            GlslBuiltinUtils.getBuiltinConstants(project).values
        }
    }

    fun testBuiltinFunctionsAreScopedPerProject() {
        assertCacheIsScopedPerProject("builtin functions") { project ->
            GlslBuiltinUtils.getBuiltinFuncs(project).values.flatten()
        }
    }

    fun testVectorStructsAreScopedPerProject() {
        assertCacheIsScopedPerProject("vector struct members") { project ->
            GlslBuiltinUtils.getVecStructs(project).values.flatMap { it.values }
        }
    }

    fun testShaderVariablesAreScopedPerProject() {
        assertCacheIsScopedPerProject("shader variables") { project ->
            GlslBuiltinUtils.getShaderVariables(project).values
        }
    }

    /**
     * Builds the cache read by [lookup] against a temporary project, disposes that project, and then
     * checks that this test's own project gets its own valid elements rather than the disposed
     * project's stale ones.
     */
    private fun assertCacheIsScopedPerProject(cacheName: String, lookup: (Project) -> Collection<PsiElement>) {
        withTemporaryProject("builtinCacheOrigin") { temporaryProject ->
            val elements = lookup(temporaryProject)
            assertFalse("expected $cacheName to be found in the temporary project", elements.isEmpty())
            assertTrue(
                "expected $cacheName in the temporary project to be valid before it's disposed",
                elements.all { it.isValid }
            )
        }

        myFixture.configureByText("cache.glsl", "void main() {}")
        val elements = lookup(project)
        assertFalse("expected $cacheName to be found in this project", elements.isEmpty())
        assertTrue(
            "expected all $cacheName in this project to be valid after another project was disposed",
            elements.all { it.isValid }
        )
        assertTrue(
            "expected all $cacheName in this project to belong to this project, not the disposed one",
            elements.all { it.project == project }
        )
    }
}
