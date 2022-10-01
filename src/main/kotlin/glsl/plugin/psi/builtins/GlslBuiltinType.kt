package glsl.plugin.psi.builtins

import com.intellij.psi.PsiElement
import glsl.plugin.psi.GlslType
import glsl.plugin.psi.named.GlslNamedElement

class GlslBuiltinType(private val psiElement: PsiElement) : GlslType {

    /**
     *
     */
    override fun getTypeText(): String {
        return psiElement.text
    }

        /**
    *
    */
    override fun getBinaryExprType(rightExprType: GlslType, expr: PsiElement?): GlslType? {
        return null
    }

    /**
    *
    */
    override fun getStructMembers(): List<GlslNamedElement> {
        return emptyList()
    }

    /**
    *
    */
    override fun getStructMember(memberName: String): GlslNamedElement? {
        return null
    }

    /**
     *
     */
    override fun getDimension(): Int {
        return 1
    }

    /**
     *
     */
    override fun isConvertible(other: String): Boolean {
        return false
    }

}
