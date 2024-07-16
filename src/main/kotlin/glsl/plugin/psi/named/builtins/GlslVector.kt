package glsl.plugin.psi.named.builtins

import com.intellij.lang.ASTNode
import glsl.data.GlslDefinitions
import glsl.plugin.psi.named.GlslNamedBuiltinType
import glsl.plugin.psi.named.GlslNamedVariable
import glsl.plugin.utils.GlslBuiltinUtils


/**
 *
 */
abstract class GlslVector(node: ASTNode) : GlslNamedBuiltinType(node) {
    /**
     *
     */
    override fun getPsi(): GlslVector {
        return this
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
    override fun isConvertible(other: String): Boolean {
        val implicitConversions = GlslDefinitions.VECTORS[name]
        return implicitConversions?.contains(other) ?: false
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
