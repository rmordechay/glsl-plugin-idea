package glsl.plugin.editor

import com.intellij.icons.AllIcons
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.structureView.*
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement
import com.intellij.ide.util.treeView.smartTree.Sorter
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.lang.PsiStructureViewFactory
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import glsl.plugin.language.GlslFile
import glsl.psi.impl.GlslFunctionDefinitionImpl
import glsl.psi.interfaces.GlslExternalDeclaration
import glsl.psi.interfaces.GlslFunctionDefinition


class GlslStructureViewFactory : PsiStructureViewFactory {
    override fun getStructureViewBuilder(psiFile: PsiFile): StructureViewBuilder {
        return object : TreeBasedStructureViewBuilder() {
            override fun createStructureViewModel(editor: Editor?): StructureViewModel {
                return GlslStructureViewModel(editor, psiFile)
            }
        }
    }
}

class GlslStructureViewModel(editor: Editor?, psiFile: PsiFile) :
    StructureViewModelBase(psiFile, editor, GlslStructureViewElement(psiFile)), StructureViewModel.ElementInfoProvider {

    override fun getSorters(): Array<Sorter> {
        return arrayOf(Sorter.ALPHA_SORTER)
    }

    override fun getSuitableClasses(): Array<Class<GlslExternalDeclaration>> {
        return arrayOf(GlslExternalDeclaration::class.java)
    }

    override fun isAlwaysShowsPlus(element: StructureViewTreeElement?): Boolean {
        return false
    }

    override fun isAlwaysLeaf(element: StructureViewTreeElement?): Boolean {
        return element?.value is GlslExternalDeclaration
    }
}


class GlslStructureViewElement(private val element: NavigatablePsiElement) : StructureViewTreeElement, SortableTreeElement {

    override fun getValue(): Any {
        return element
    }

    override fun getPresentation(): ItemPresentation {
        if (element is GlslFunctionDefinition) {
            return PresentationData(element.functionDeclarator.text + ")", "", AllIcons.Nodes.Function, HighlighterColors.NO_HIGHLIGHTING)
        } else if (element is GlslFile) {
            return element.presentation ?: PresentationData()
        }
        return PresentationData()
    }

    override fun getChildren(): Array<TreeElement> {
        if (element !is GlslFile) return emptyArray()
        val externalDeclarations = PsiTreeUtil.getChildrenOfType(element, GlslExternalDeclaration::class.java) ?: return emptyArray()
        val funcs = mutableListOf<TreeElement>()
        for (externalDeclaration in externalDeclarations) {
            if (externalDeclaration.functionDefinition == null) continue
            funcs.add(GlslStructureViewElement(externalDeclaration.functionDefinition!! as GlslFunctionDefinitionImpl))
        }
        return funcs.toTypedArray()
    }

    override fun getAlphaSortKey(): String {
        return element.name ?: ""
    }

    override fun navigate(requestFocus: Boolean) {
        element.navigate(requestFocus)
    }

    override fun canNavigate(): Boolean {
        return element.canNavigate()
    }
    override fun canNavigateToSource(): Boolean {
        return element.canNavigateToSource()
    }
}
