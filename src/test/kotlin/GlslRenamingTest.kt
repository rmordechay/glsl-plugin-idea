import com.intellij.codeInsight.template.impl.TemplateManagerImpl
import com.intellij.ide.DataManager
import com.intellij.injected.editor.EditorWindow
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.util.Disposer
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.refactoring.rename.PsiElementRenameHandler
import com.intellij.refactoring.rename.RenameProcessor
import com.intellij.refactoring.rename.inplace.MemberInplaceRenameHandler
import com.intellij.testFramework.PlatformTestUtil
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
    // tests the full in-place rename chain in a GLSL file
    fun testInplaceRenamingIdentifier() {
        myFixture.configureByFile("RenamingIdentifierFile7.glsl")
        renameInPlace("func_updated")
        myFixture.checkResultByFile("RenamingIdentifierFile7Expected.glsl")
    }

    // in-place rename of GLSL injected in an HTML script (also verifies that other languages stay unchange)
    fun testInplaceRenamingIdentifierInInjectedHtml() {
        myFixture.configureByFile("RenamingIdentifierFile8.html")
        renameInPlace("func_updated")
        myFixture.checkResultByFile("RenamingIdentifierFile8Expected.html")
    }

    // renames GLSL injected in HTML even when the refactoring scope excludes the host file, as for library sources
    fun testRenamingIdentifierInInjectedHtmlOutsideProjectScope() {
        myFixture.configureByFile("RenamingIdentifierFile8.html")
        RenameProcessor(project, myFixture.elementAtCaret, "func_updated", GlobalSearchScope.EMPTY_SCOPE, false, false).run()
        myFixture.checkResultByFile("RenamingIdentifierFile8Expected.html")
    }

    // runs the full in-place rename chain (not just the processor as renameElementAtCaret does)
    private fun renameInPlace(newName: String) {
        val editorContext = DataManager.getInstance().getDataContext(myFixture.editor.contentComponent)

        val context = AnActionEvent.getInjectedDataContext(editorContext)
        val element = PsiElementRenameHandler.getElement(context) as? PsiNamedElement ?: error("no element to rename at caret")
        val oldName = Regex("\\b" + Regex.escape(element.name ?: error("element has no name")) + "\\b")

        val editor = CommonDataKeys.EDITOR.getData(context) ?: myFixture.editor
        val topLevelEditor = (editor as? EditorWindow)?.delegate ?: editor
        val fragmentRange = InjectedLanguageManager.getInstance(project).injectedToHost(element, element.containingFile.textRange)
        val fragmentMarker = topLevelEditor.document.createRangeMarker(fragmentRange)
        val templateTesting = Disposer.newDisposable()

        try {
            TemplateManagerImpl.setTemplateTesting(templateTesting)
            MemberInplaceRenameHandler().doRename(element, editor, context)

            val range = TemplateManagerImpl.getTemplateState(topLevelEditor)?.currentVariableRange
                ?: error("in-place rename did not start")

            WriteCommandAction.writeCommandAction(project).run<RuntimeException> {
                topLevelEditor.document.replaceString(range.startOffset, range.endOffset, newName)
            }

            val state = TemplateManagerImpl.getTemplateState(topLevelEditor) ?: error("template vanished after typing")
            state.gotoEnd(false)
            // the refactoring runs after the template finishes, on a non-blocking read action; the document shows the result
            PlatformTestUtil.waitWithEventsDispatching(
                "rename did not complete",
                { !oldName.containsMatchIn(topLevelEditor.document.getText(fragmentMarker.textRange)) },
                10
            )
        } finally {
            fragmentMarker.dispose()
            Disposer.dispose(templateTesting)
        }
    }
}
