package glsl.plugin.psi.named.types.builtins

import com.intellij.lang.ASTNode
import com.intellij.psi.tree.IElementType
import glsl.data.GlslDefinitions
import glsl.plugin.psi.named.GlslNamedTypeImpl
import glsl.plugin.psi.named.GlslNamedVariable
import glsl.psi.interfaces.GlslBuiltinTypeScalar

/**
 *
 */
abstract class GlslScalar(node: ASTNode) : GlslNamedTypeImpl(node), GlslBuiltinType {
    var literalElementType: IElementType? = null

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
    override fun canCast(other: IElementType?): Boolean {
        if (other == null) return false
        val implicitConversions = GlslDefinitions.SCALARS[other]
        return implicitConversions?.contains(typeAsToken()) ?: false
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
    override fun typeAsToken(): IElementType? {
        if (literalElementType != null) {
            return literalElementType
        }
        return super.typeAsToken()
    }
}
