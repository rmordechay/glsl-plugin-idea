package glsl.plugin.psi.named.types.builtins

import com.intellij.lang.ASTNode
import com.intellij.psi.tree.IElementType
import glsl.data.GlslDefinitions
import glsl.plugin.inspections.GlslError
import glsl.plugin.inspections.GlslErrorCode
import glsl.plugin.psi.named.GlslNamedElement
import glsl.plugin.psi.named.GlslNamedType
import glsl.plugin.psi.named.GlslNamedTypeImpl
import glsl.plugin.psi.named.GlslNamedVariable
import glsl.psi.interfaces.GlslBuiltinTypeMatrix

/**
 *
 */
abstract class GlslMatrix(node: ASTNode) : GlslBuiltinType(node) {
    override var isPrimitive = true

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
        when (other) {
            is GlslScalar -> return this
            is GlslVector -> {
                if (operation == "*") return this
                val msg = GlslErrorCode.DOES_NOT_OPERATE_ON.message.format(operation, name, other.name)
                glslError = GlslError(GlslErrorCode.DOES_NOT_OPERATE_ON, msg)
                return this
            }
            is GlslMatrix -> return this
        }
        return null
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



