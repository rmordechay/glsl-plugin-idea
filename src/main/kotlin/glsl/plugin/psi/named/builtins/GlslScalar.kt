package glsl.plugin.psi.named.builtins

import com.intellij.lang.ASTNode
import glsl.data.GlslDefinitions
import glsl.plugin.psi.named.GlslNamedBuiltinType
import glsl.plugin.psi.named.GlslNamedVariable

/**
 *
 */
abstract class GlslScalar(node: ASTNode) : GlslNamedBuiltinType(node) {

    /**
     *
     */
    override fun getPsi(): GlslScalar {
        return this
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
