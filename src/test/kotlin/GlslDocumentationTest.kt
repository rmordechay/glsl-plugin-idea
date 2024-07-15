
import com.intellij.codeInsight.documentation.DocumentationManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import glsl.psi.interfaces.GlslFunctionCall
import glsl.psi.interfaces.GlslSingleDeclaration
import glsl.psi.interfaces.GlslUnaryExpr
import org.junit.Test


class GlslDocumentationTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String {
        return "src/test/testData/documentation"
    }

    fun testDocumentationFile1() {
        myFixture.configureByFile("DocumentationFile1.glsl")
        val originalElement = myFixture.elementAtCaret as GlslSingleDeclaration
        val glslPostfixExpr = originalElement.exprNoAssignmentList.first() as GlslUnaryExpr
        val variableIdentifier = (glslPostfixExpr.postfixExpr as GlslFunctionCall).variableIdentifier
        val element = DocumentationManager
            .getInstance(project)
            .findTargetElement(myFixture.editor, variableIdentifier?.containingFile, variableIdentifier)
        val documentationProvider = DocumentationManager.getProviderFromElement(element)
        val doc = documentationProvider.generateDoc(element, originalElement)
        assertNotNull(doc)
        assertTrue(doc!!.contains("<div id=\"abs\">"))
    }

//    fun testDocumentationFile2() {
//        myFixture.configureByFile("DocumentationFile2.glsl")
//        val originalElement = myFixture.elementAtCaret
//        val element = DocumentationManager
//            .getInstance(project)
//            .findTargetElement(myFixture.editor, originalElement.containingFile, originalElement)
//        val documentationProvider = DocumentationManager.getProviderFromElement(element)
//        val doc = documentationProvider.generateDoc(element, originalElement)
//        assertNotNull(doc)
//        assertTrue(doc!!.contains("Function documentation"))
//    }
}