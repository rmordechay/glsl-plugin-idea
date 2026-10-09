import com.intellij.openapi.project.Project
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory

/**
 * Runs [block] against a second, short-lived project, then disposes that project - just like a user
 * closing a project in a running IDE. Use it in tests that check the plugin doesn't keep state from
 * one project alive and leak it into another (e.g. a [com.intellij.testFramework.fixtures.BasePlatformTestCase]'s
 * own project).
 *
 * The project is disposed even if [block] throws.
 */
fun withTemporaryProject(name: String, block: (Project) -> Unit) {
    val projectFixture = IdeaTestFixtureFactory.getFixtureFactory().createFixtureBuilder(name).fixture
    projectFixture.setUp()
    try {
        block(projectFixture.project)
    } finally {
        projectFixture.tearDown()
    }
}
