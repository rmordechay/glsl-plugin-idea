package glsl.plugin.psi.builtins

import com.intellij.lang.ASTNode
import glsl.data.GlslDefinitions
import glsl.plugin.psi.named.GlslNamedVariable

/**
 *
 */
abstract class GlslMatrix(node: ASTNode) : GlslBuiltinType(node) {

    /**
     *
     */
    override fun getStructMembers(): List<GlslNamedVariable> {
//        val lengthFunc = GlslBuiltinUtils.getVecComponent("length") ?: return emptyList()
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
        val implicitConversions = GlslDefinitions.MATRICES[name]
        return implicitConversions?.contains(other) ?: false
    }

    /**
     *
     */
    override fun getDimension(): Int {
        val lastChar = name.last()
        return when (lastChar) {
            '2' -> 2
            '3' -> 3
            '4' -> 4
            else -> 0
        }
    }


//    /**
//     *
//     */
//    fun getColumnDimension(): Int? {
//        return name?.last()?.digitToInt()
//    }
//
//    /**
//     *
//     */
//    fun getRowDimension(): Int {
//        val dims = dimensionsRegex.find(name)?.groupValues ?: return 0
//        if (dims[2].isNotEmpty()) {
//            return dims[2].toInt()
//        } else if (dims[3].isNotEmpty()) {
//            return dims[3].toInt()
//        }
//        return 0
//    }

    /**
     *
     */
    private fun getMatrixComponentType(): String {
        val typeText = name.first()
        return when (typeText) {
            'm', 'f' -> "float"
            'd' -> "double"
            else -> ""
        }
    }
}



