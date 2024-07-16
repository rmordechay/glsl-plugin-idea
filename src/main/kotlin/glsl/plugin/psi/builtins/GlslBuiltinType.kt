package glsl.plugin.psi.builtins

import com.intellij.lang.ASTNode
import glsl.plugin.psi.GlslType
import glsl.plugin.psi.named.GlslNamedVariable

/**
 *
 */
abstract class GlslBuiltinType(node: ASTNode) : GlslType(node) {

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
        return false
    }

    /**
     *
     */
    override fun getDimension(): Int {
        return 1
    }

}