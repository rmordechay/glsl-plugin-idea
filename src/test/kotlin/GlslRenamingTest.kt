import com.intellij.refactoring.util.CommonRefactoringUtil.RefactoringErrorHintException
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class GlslRenamingTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String {
        return "src/test/testData/renaming"
    }

    fun testRenamingIdentifierFile1() {
        myFixture.configureByFile("RenamingIdentifierFile1.glsl")
        myFixture.renameElementAtCaret("newName")
        myFixture.checkResultByFile("RenamingIdentifierExpectedFile1.glsl")
    }

    fun testRenamingIdentifierFile2() {
        myFixture.configureByFile("RenamingIdentifierFile2.glsl")
        assertThrows(RefactoringErrorHintException::class.java) { myFixture.renameElementAtCaret("newName") }
    }

    fun testRenamingTypeFile() {
        myFixture.configureByFile("RenamingTypeFile.glsl")
        myFixture.renameElementAtCaret("NewName")
        myFixture.checkResultByFile("RenamingTypeExpectedFile.glsl")
    }

    fun testRenamingMacroObjectFile() {
        myFixture.configureByFile("RenamingMacroObjectFile.glsl")
        myFixture.renameElementAtCaret("VAR_UPDATED")
        myFixture.checkResultByFile("RenamingMacroObjectExpectedFile.glsl")
    }

    fun testRenamingMacroFuncFile() {
        myFixture.configureByFile("RenamingMacroFuncFile.glsl")
        myFixture.renameElementAtCaret("f_updated")
        myFixture.checkResultByFile("RenamingMacroFuncExpectedFile.glsl")
    }
}