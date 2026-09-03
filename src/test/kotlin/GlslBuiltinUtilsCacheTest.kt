import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory
import glsl.plugin.utils.GlslBuiltinUtils

/**
 * GlslBuiltinUtils's builtin-element caches are scoped per-project. Verifies that disposing the
 * project a cache was built against doesn't leak stale elements into an unrelated project.
 */
class GlslBuiltinUtilsCacheTest : BasePlatformTestCase() {

    fun testBuiltinCacheIsScopedPerProject() {
        val disposableProjectFixture: IdeaProjectTestFixture =
            IdeaTestFixtureFactory.getFixtureFactory().createFixtureBuilder("builtinCacheOrigin").fixture
        disposableProjectFixture.setUp()
        try {
            val constants = GlslBuiltinUtils.getBuiltinConstants(disposableProjectFixture.project)
            assertFalse(constants.isEmpty())
            assertTrue(constants.values.first().isValid)
        } finally {
            // Dispose the project the cache was built against - exactly what happens when a
            // user closes a project in the real IDE.
            disposableProjectFixture.tearDown()
        }

        // An unrelated project (this test's own fixture) must get its own, independently valid
        // builtin elements, not the disposed project's stale ones.
        myFixture.configureByText("cache.glsl", "void main() {}")
        val constantsInThisProject = GlslBuiltinUtils.getBuiltinConstants(project)
        assertFalse(constantsInThisProject.isEmpty())
        val element = constantsInThisProject.values.first()
        assertTrue("builtin constants resolved for this project must be valid", element.isValid)
        assertEquals(project, element.project)
    }
}
