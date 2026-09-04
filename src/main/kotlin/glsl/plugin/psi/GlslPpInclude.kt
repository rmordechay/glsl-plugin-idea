package glsl.plugin.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiReference
import glsl.plugin.reference.GlslIncludeFileReference
import glsl.psi.interfaces.GlslPpIncludeDeclaration

abstract class GlslPpInclude(node: ASTNode) :
    ASTWrapperPsiElement(node),
    GlslPpIncludeDeclaration {

    override fun getReference(): PsiReference? {
        val pathElement = stringLiteral ?: includePath ?: return null

        val range = TextRange(
            pathElement.startOffsetInParent,
            pathElement.startOffsetInParent + pathElement.textLength
        )

        return GlslIncludeFileReference(this, range)
    }
}