package glsl.plugin.psi.named.types.builtins

import com.intellij.lang.ASTNode
import com.intellij.psi.tree.IElementType
import glsl.plugin.psi.named.GlslNamedTypeImpl
import glsl.plugin.psi.named.GlslNamedVariable
import glsl.psi.interfaces.GlslBuiltinTypeRest

/**
 *
 */
abstract class GlslBuiltinRest(node: ASTNode) : GlslNamedTypeImpl(node), GlslBuiltinType {

    /**
     *
     */
    override fun getPsi(): GlslBuiltinTypeRest {
        return this as GlslBuiltinTypeRest
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
        return false
    }

    /**
     *
     */
    override fun getDimension(): Int {
        return 1
    }
}
