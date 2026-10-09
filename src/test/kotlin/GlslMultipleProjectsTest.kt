import com.intellij.codeInsight.completion.CompletionType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import glsl.plugin.language.GlslFileType
import glsl.psi.interfaces.GlslFunctionDeclarator

private const val BUILTIN_CALL = "void main() { float x = abs(1.0); }"

/**
 * Simulates a user working in one project, closing it, and continuing in another: builtin resolution and
 * completion must keep working in the remaining project. Unlike [GlslBuiltinUtilsCacheTest], this goes through
 * the user-facing features, so it also catches stale state held anywhere else in the plugin.
 */
class GlslMultipleProjectsTest : BasePlatformTestCase() {

    fun testBuiltinFunctionResolvesAfterAnotherProjectIsClosed() {
        withTemporaryProject("closedProject", ::resolveBuiltinCallIn)

        myFixture.configureByText("main.glsl", BUILTIN_CALL.replace("abs(", "ab<caret>s("))
        val resolved = myFixture.getReferenceAtCaretPosition()?.resolve()

        assertTrue("expected abs() to resolve to a builtin function declaration, got $resolved", resolved is GlslFunctionDeclarator)
        assertTrue("expected the resolved builtin function to be valid", resolved?.isValid == true)
        assertEquals(
            "expected the builtin function to resolve within this project, not the closed one",
            project,
            resolved?.project
        )
    }

    fun testBuiltinFunctionCompletionWorksAfterAnotherProjectIsClosed() {
        withTemporaryProject("closedProject", ::resolveBuiltinCallIn)

        myFixture.configureByText("main.glsl", "void main() { float x = ab<caret> }")
        myFixture.complete(CompletionType.BASIC)
        val lookupStrings = myFixture.lookupElementStrings

        assertNotNull("expected a completion popup with several candidates rather than a single auto-inserted one", lookupStrings)
        assertTrue(
            "expected builtin abs() to be offered after another project was closed, got $lookupStrings",
            lookupStrings.orEmpty().contains("abs(float x)")
        )
    }

    /**
     * Resolves a builtin function call in [project], so that everything the plugin builds or caches while
     * resolving exists for that project before it gets closed.
     */
    private fun resolveBuiltinCallIn(project: Project) {
        val file = PsiFileFactory.getInstance(project).createFileFromText("other.glsl", GlslFileType(), BUILTIN_CALL)
        val resolved = file.findReferenceAt(BUILTIN_CALL.indexOf("abs") + 1)?.resolve()
        assertTrue(
            "expected abs() to resolve to a builtin function declaration in the project about to be closed, got $resolved",
            resolved is GlslFunctionDeclarator
        )
    }
}
