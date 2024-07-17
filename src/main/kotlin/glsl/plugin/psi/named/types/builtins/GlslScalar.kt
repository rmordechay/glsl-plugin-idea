package glsl.plugin.psi.named.types.builtins

import com.intellij.lang.ASTNode
import glsl.data.GlslDefinitions
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
    override fun isConvertible(other: String?): Boolean {
        if (other == null) return false
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
