import com.intellij.refactoring.util.CommonRefactoringUtil.RefactoringErrorHintException
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.Test

class GlslRenamingTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String {
        return "src/test/testData/renaming"
    }

    @Test
    fun testRenamingIdentifierFile1() {
        myFixture.configureByFile("RenamingIdentifierFile1.glsl")
        myFixture.renameElementAtCaret("newName")
        myFixture.checkResultByFile("RenamingIdentifierExpectedFile1.glsl")
    }

    @Test
    fun testRenamingIdentifierFile2() {
        myFixture.configureByFile("RenamingIdentifierFile2.glsl")
        assertThrows(RefactoringErrorHintException::class.java) { myFixture.renameElementAtCaret("newName") }
    }

    @Test
    fun testRenamingTypeFile() {
        myFixture.configureByFile("RenamingTypeFile.glsl")
        myFixture.renameElementAtCaret("NewName")
        myFixture.checkResultByFile("RenamingTypeExpectedFile.glsl")
    }

    @Test
    fun testRenamingMacroFile() {
        myFixture.configureByFile("RenamingMacroFile.glsl")
        myFixture.renameElementAtCaret("NewName")
        myFixture.checkResultByFile("RenamingMacroExpectedFile.glsl")
    }
}