package glsl.plugin.psi.named.types.builtins

import com.intellij.lang.ASTNode
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.elementType
import glsl.data.GlslDefinitions
import glsl.data.GlslErrorMessages
import glsl.plugin.psi.named.GlslNamedElement
import glsl.plugin.psi.named.GlslNamedType
import glsl.plugin.psi.named.GlslNamedTypeImpl
import glsl.plugin.psi.named.GlslNamedVariable
import glsl.plugin.utils.GlslBuiltinUtils
import glsl.psi.interfaces.GlslBuiltinTypeVector


/**
 *
 */
abstract class GlslVector(node: ASTNode) : GlslNamedTypeImpl(node), GlslBuiltinType {

    /**
     *
     */
    override fun getPsi(): GlslBuiltinTypeVector {
        return this as GlslBuiltinTypeVector
    }

    /**
     *
     */
    override fun getStructMembers(): List<GlslNamedVariable> {
        val vectorComponents = GlslBuiltinUtils.getVecStructs()[name]?.values?.mapNotNull { it as? GlslNamedVariable }
        return vectorComponents ?: emptyList()
    }

    /**
     *
     */
    override fun getStructMember(memberName: String): GlslNamedVariable? {
        return getStructMembers().find { it.name == memberName }
    }

    /**
     *
     */
    override fun getBinaryType(other: GlslNamedElement?, operation: String): GlslNamedType? {
        when (other) {
            is GlslScalar -> return this
            is GlslVector -> return this
            is GlslMatrix -> {
                if (operation == "*") return this
                errorMessage = GlslErrorMessages.DOES_NOT_OPERATE_ON.format(operation, name, other.name)
                return this
            }
        }
        return null
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

    /**
     *
     */
    override fun canCast(other: IElementType?): Boolean {
        if (other == null) return false
        val implicitConversions = GlslDefinitions.VECTORS[other]
        return implicitConversions?.contains(elementType) ?: false
    }

    /**
     *
     */
    private fun getVectorComponentType(): String {
        val typeText = name?.first()
        return when (typeText) {
            'v', 'f' -> "float"
            'd' -> "double"
            'u' -> "uint"
            'i' -> "int"
            'b' -> "bool"
            else -> ""
        }
    }
}
