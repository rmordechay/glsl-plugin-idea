import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.util.containers.ContainerUtil
import org.junit.Test

class GlslFormatterTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String {
        return "src/test/testData/formatter"
    }

    @Test
    fun testFormatterFile() {
        myFixture.configureByFile("FormatterFile.glsl")
        WriteCommandAction.writeCommandAction(project).run<RuntimeException> {
            val codeStyleManager = CodeStyleManager.getInstance(project)
            codeStyleManager.reformatText(myFixture.file, ContainerUtil.newArrayList(myFixture.file.textRange))
        }
        myFixture.checkResultByFile("FormatterFileExpected.glsl")
    }

    @Test
    fun testFormatterFile2() {
        myFixture.configureByFile("FormatterFile2.glsl")
        WriteCommandAction.writeCommandAction(project).run<RuntimeException> {
            val codeStyleManager = CodeStyleManager.getInstance(project)
            codeStyleManager.reformatText(myFixture.file, ContainerUtil.newArrayList(myFixture.file.textRange))
        }
        myFixture.checkResultByFile("FormatterFileExpected2.glsl")
    }
}