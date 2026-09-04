package glsl.plugin.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiReference
import glsl.plugin.reference.GlslImportFileReference
import glsl.psi.interfaces.GlslPpImportDeclaration

abstract class GlslPpImport(node: ASTNode) :
    ASTWrapperPsiElement(node),
    GlslPpImportDeclaration {

    override fun getReference(): PsiReference? {
        val ns = identifier ?: return null
        val path = includePath ?: return null

        val start = ns.startOffsetInParent
        val end = path.startOffsetInParent + path.textLength

        val range = TextRange(start, end)

        return GlslImportFileReference(this, range)
    }
}