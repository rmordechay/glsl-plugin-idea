import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.util.containers.ContainerUtil

class GlslFormatterTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String {
        return "src/test/testData/formatter"
    }

    fun testFormatter() {
        myFixture.configureByFile("formatterFile.glsl")
        WriteCommandAction.writeCommandAction(project).run<RuntimeException> {
            val codeStyleManager = CodeStyleManager.getInstance(project)
            codeStyleManager.reformatText(myFixture.file, listOf(myFixture.file.textRange))
        }
        myFixture.checkResultByFile("formatterFileExpected.glsl")
    }
}