package glsl.plugin.psi.named.types.builtins

import com.intellij.lang.ASTNode
import com.intellij.psi.tree.IElementType
import glsl.data.GlslDefinitions
import glsl.plugin.psi.named.GlslNamedElement
import glsl.plugin.psi.named.GlslNamedType
import glsl.plugin.psi.named.GlslNamedTypeImpl
import glsl.plugin.psi.named.GlslNamedVariable
import glsl.psi.interfaces.GlslBuiltinTypeScalar

/**
 *
 */
abstract class GlslScalar(node: ASTNode) : GlslNamedTypeImpl(node), GlslBuiltinType {
    /**
     *
     */
    override fun getPsi(): GlslBuiltinTypeScalar {
        return this as GlslBuiltinTypeScalar
    }

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
    override fun getBinaryType(other: GlslNamedElement?, operation: String): GlslNamedType? {
        return when (other) {
            is GlslScalar -> this
            is GlslVector -> other
            is GlslMatrix -> other
            else -> null
        }
    }

    /**
     *
     */
    override fun canCast(other: IElementType?): Boolean {
        if (other == null) return false
        val implicitConversions = GlslDefinitions.SCALARS[other]
        val element = typeAsToken()
        return implicitConversions?.contains(element) ?: false
    }

    /**
     *
     */
    override fun getDimension(): Int {
        return 1
    }
}
