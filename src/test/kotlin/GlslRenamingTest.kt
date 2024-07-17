import com.intellij.testFramework.fixtures.BasePlatformTestCase

class GlslRenamingTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String {
        return "src/test/testData/renaming"
    }

    fun testRenamingIdentifier1() {
        myFixture.configureByFile("RenamingIdentifierFile1.glsl")
        myFixture.renameElementAtCaret("newName")
        myFixture.checkResultByFile("RenamingIdentifierFile1Expected.glsl")
    }

    fun testRenamingIdentifier2() {
        myFixture.configureByFile("RenamingIdentifierFile2.glsl")
        assertThrows(RuntimeException::class.java) { myFixture.renameElementAtCaret("newName") }
    }

    fun testRenamingIdentifier3() {
        myFixture.configureByFile("RenamingIdentifierFile3.glsl")
        myFixture.renameElementAtCaret("f_updated")
        myFixture.checkResultByFile("RenamingIdentifierFile3Expected.glsl")
    }

    fun testRenamingIdentifier4() {
        myFixture.configureByFile("RenamingIdentifierFile4.glsl")
        myFixture.renameElementAtCaret("VAR_UPDATED")
        myFixture.checkResultByFile("RenamingIdentifierFile4Expected.glsl")
    }

    fun testRenamingIdentifier5() {
        myFixture.configureByFile("RenamingIdentifierFile5.glsl")
        myFixture.renameElementAtCaret("NewName")
        myFixture.checkResultByFile("RenamingIdentifierFile5Expected.glsl")
    }

    fun testRenamingIdentifier6() {
        myFixture.configureByFile("RenamingIdentifierFile6.glsl")
        myFixture.renameElementAtCaret("TexCoordUpdated")
        myFixture.checkResultByFile("RenamingIdentifierFile6Expected.glsl")
    }

    fun testRenamingIdentifier7() {
        myFixture.configureByFile("RenamingIdentifierFile7.glsl")
        myFixture.renameElementAtCaret("func_updated")
        myFixture.checkResultByFile("RenamingIdentifierFile7Expected.glsl")
    }

    fun testRenamingIdentifier8() {
        myFixture.configureByFile("RenamingIdentifierFile8.html")
        myFixture.renameElementAtCaret("func_updated")
        myFixture.checkResultByFile("RenamingIdentifierFile8Expected.html")
    }

    fun testRenamingIdentifier9() {
        myFixture.configureByFile("RenamingIdentifierFile9.glsl")
        myFixture.renameElementAtCaret("func_updated")
        myFixture.checkResultByFile("RenamingIdentifierFile9Expected.glsl")
    }
}