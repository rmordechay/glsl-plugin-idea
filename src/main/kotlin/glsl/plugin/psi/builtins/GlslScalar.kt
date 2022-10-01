package glsl.plugin.psi.builtins

import com.intellij.psi.PsiElement
import glsl.data.GlslDefinitions
import glsl.plugin.psi.GlslType
import glsl.plugin.psi.named.GlslNamedElement

class GlslScalar(private val typeText: String) : GlslType {

    /**
     *
     */
    override fun getTypeText(): String {
        return typeText
    }

    /**
     *
     */
    override fun getBinaryExprType(rightExprType: GlslType, expr: PsiElement?): GlslType {
        if (rightExprType is GlslScalar) {
            return this
        } else if (rightExprType is GlslVector || rightExprType is GlslMatrix) {
            return rightExprType
        }
        return this
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
    override fun isConvertible(other: String): Boolean {
        val implicitConversions = GlslDefinitions.SCALARS[getTypeText()]
        return implicitConversions?.contains(other) ?: false
    }

    /**
     *
     */
    override fun getDimension(): Int {
        return 1
    }
}
