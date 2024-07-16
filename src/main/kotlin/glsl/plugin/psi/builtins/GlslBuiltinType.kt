package glsl.plugin.psi.builtins

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import glsl.plugin.psi.GlslType
import glsl.plugin.psi.named.GlslNamedType
import glsl.plugin.psi.named.GlslNamedVariable

/**
 *
 */
abstract class GlslBuiltinType(node: ASTNode) : GlslType(node) {

    /**
     *
     */
    override fun getStructMembers(): List<GlslNamedVariable> {
        TODO("Not yet implemented")
    }

    /**
     *
     */
    override fun getStructMember(memberName: String): GlslNamedVariable? {
        TODO("Not yet implemented")
    }

    /**
     *
     */
    override fun isConvertible(other: String): Boolean {
        TODO("Not yet implemented")
    }

    /**
     *
     */
    override fun getDimension(): Int {
        TODO("Not yet implemented")
    }

    /**
     *
     */
    override fun getBinaryExprType(rightExprType: GlslNamedType?, expr: PsiElement?): GlslNamedType? {
        TODO("Not yet implemented")
    }
}