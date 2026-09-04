package glsl.plugin.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import glsl.plugin.utils.GlslUtils.getImportPsiFile

import glsl.psi.interfaces.GlslPpImportDeclaration

class GlslImportFileReference(
    element: GlslPpImportDeclaration,
    rangeInElement: TextRange
) : PsiReferenceBase<GlslPpImportDeclaration>(element, rangeInElement) {

    override fun resolve(): PsiElement? {
        return getImportPsiFile(element.project, element).firstOrNull()
    }

    override fun getVariants(): Array<Any> = emptyArray()
}