package glsl.plugin.psi.named.types.builtins

import com.intellij.lang.ASTNode
import com.intellij.psi.tree.IElementType
import glsl.data.GlslDefinitions
import glsl.plugin.psi.named.GlslNamedElement
import glsl.plugin.psi.named.GlslNamedType
import glsl.plugin.psi.named.GlslNamedTypeImpl
import glsl.plugin.psi.named.GlslNamedVariable
import glsl.psi.interfaces.GlslBuiltinTypeMatrix

/**
 *
 */
abstract class GlslMatrix(node: ASTNode) : GlslNamedTypeImpl(node), GlslBuiltinType {


    /**
     *
     */
    override fun getPsi(): GlslBuiltinTypeMatrix {
        return this as GlslBuiltinTypeMatrix
    }

    /**
     *
     */
    override fun getStructMembers(): List<GlslNamedVariable> {
//        val lengthFunc = GlslBuiltinUtils.getVecComponent("length") ?: return emptyList()
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
    override fun getBinaryOpType(other: GlslNamedElement?, isMultiply: Boolean): GlslNamedType? {
        when {
            other is GlslScalar -> return this
            other is GlslVector && isMultiply -> return other
            other is GlslMatrix -> return this
            else -> return null
        }
    }


    /**
     *
     */
    override fun canCast(other: IElementType?): Boolean {
        if (other == null) return false
        val implicitConversions = GlslDefinitions.MATRICES[other]
        return implicitConversions?.contains(other) ?: false
    }

    /**
     *
     */
    override fun getDimension(): Int {
        val lastChar = name?.last()
        return when (lastChar) {
            '2' -> 2
            '3' -> 3
            '4' -> 4
            else -> 0
        }
    }
}



