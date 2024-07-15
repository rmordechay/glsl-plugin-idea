package glsl.plugin.psi.builtins

import com.intellij.lang.ASTNode
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement
import glsl.data.GlslDefinitions
import glsl.plugin.psi.named.GlslNamedElement
import glsl.plugin.psi.named.GlslNamedType
import glsl.plugin.psi.named.GlslNamedTypeImpl
import glsl.plugin.psi.named.GlslNamedVariable
import glsl.plugin.utils.GlslBuiltinUtils
import javax.swing.Icon

val dimensionsRegex = "((\\d)x)?(\\d)$".toRegex()

class GlslMatrix(node: ASTNode) : GlslNamedTypeImpl(node)  {

    override fun getSelf(): GlslNamedElement {
        TODO("Not yet implemented")
    }

    override fun getHighlightTextAttr(): TextAttributesKey {
        TODO("Not yet implemented")
    }

    override fun getLookupIcon(): Icon? {
        TODO("Not yet implemented")
    }

    override fun getNameIdentifier(): PsiElement? {
        TODO("Not yet implemented")
    }

    /**
     *
     */
    override fun getStructMembers(): List<GlslNamedVariable> {
        val lengthFunc = GlslBuiltinUtils.getVecComponent("length") ?: return emptyList()
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
    override fun getBinaryExprType(rightExprType: GlslNamedType?, expr: PsiElement?): GlslNamedType? {
//        if (rightExprType is GlslVector) {
//            if (expr is GlslMulExpr) {
//                val leftRows = getRowDimension()
//                val leftColumn = getColumnDimension()
//                val rightRows = rightExprType.getDimension()
//                if (leftColumn == rightRows) {
//                    return GlslVector("vec$leftRows")
//                }
//            }
//            return null
//        }
//        return this
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
        val typeText = name?.first()
        return when (typeText) {
            'm', 'f' -> "float"
            'd' -> "double"
            else -> ""
        }
    }
//
//    /**
//     *
//     */
//    fun getChildType(exprList: List<GlslExpr>): GlslTypeName {
//        if (exprList.isEmpty()) {
//            return this
//        }
//        val componentType = getMatrixComponentType()
//        if (exprList.size == 1) {
//            val columnDimension = getColumnDimension()
//            if (componentType == "float") {
//                return GlslVector("vec$columnDimension")
//            } else if (componentType == "double") {
//                return GlslVector("dvec$columnDimension")
//            }
//        } else if (exprList.size == 2) {
//            return GlslScalar(componentType)
//        }
//        return this
//    }
}



