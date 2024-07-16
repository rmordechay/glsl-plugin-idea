package glsl.plugin.psi.builtins

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import glsl.data.GlslDefinitions
import glsl.plugin.psi.named.GlslNamedType
import glsl.plugin.psi.named.GlslNamedVariable

/**
 *
 */
abstract class GlslScalar(node: ASTNode) : GlslBuiltinType(node) {

    /**
     *
     */
    override fun getStructMembers(): List<GlslNamedVariable> {
        return emptyList()
    }

    /**
     *
     */
    override fun getStructMember(memberName: String): GlslNamedVariable? {
        return null
    }

    /**
     *
     */
    override fun getBinaryExprType(rightExprType: GlslNamedType?, expr: PsiElement?): GlslNamedType? {
//        if (rightExprType is GlslScalar) {
//            return this
//        } else if (rightExprType is GlslVector || rightExprType is GlslMatrix) {
//            return rightExprType
//        }
//        return this
        return null
    }


    /**
     *
     */
    override fun isConvertible(other: String): Boolean {
        val implicitConversions = GlslDefinitions.SCALARS[name]
        return implicitConversions?.contains(other) ?: false
    }

    /**
     *
     */
    override fun getDimension(): Int {
        return 1
    }
}
