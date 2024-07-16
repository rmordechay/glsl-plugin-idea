package glsl.plugin.psi.named

import com.intellij.lang.ASTNode
import glsl.plugin.psi.GlslType


/**
 *
 */
interface GlslNamedType : GlslNamedElement {
    fun getStructMembers(): List<GlslNamedVariable>
    fun getStructMember(memberName: String): GlslNamedVariable?
    fun isConvertible(other: String): Boolean
    fun getDimension(): Int

    /**
     *
     */
    fun isEqual(other: GlslType?): Boolean {
        if (other == null) return false
        val otherTypeText = other.name
        return name == otherTypeText || isConvertible(otherTypeText)
    }

    /**
     *
     */
    fun isEqual(other: String?): Boolean {
        if (other == null) return false
        return name == other || isConvertible(other)
    }
}

/**
 *
 */
abstract class GlslNamedTypeImpl(node: ASTNode) : GlslNamedElementImpl(node), GlslNamedType

