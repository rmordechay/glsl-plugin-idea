package glsl.plugin.psi.builtins

import com.intellij.psi.PsiElement
import glsl.data.GlslDefinitions
import glsl.plugin.psi.GlslType
import glsl.plugin.psi.named.GlslNamedElement
import glsl.plugin.utils.GlslBuiltinUtils
import glsl.psi.interfaces.GlslExpr
import glsl.psi.interfaces.GlslMulExpr
import glsl.psi.interfaces.GlslTypeSpecifier

val dimensionsRegex = "((\\d)x)?(\\d)$".toRegex()

class GlslMatrix(private val typeSpecifier: GlslTypeSpecifier) : GlslType {

    /**
     *
     */
    override fun getTypeText(): String {
        return typeSpecifier.text
    }

    /**
     *
     */
    override fun getStructMembers(): List<GlslNamedElement> {
        val lengthFunc = GlslBuiltinUtils.getVecComponent("length") ?: return emptyList()
        return listOf(lengthFunc)
    }

    /**
     *
     */
    override fun getStructMember(memberName: String): GlslNamedElement? {
        return GlslBuiltinUtils.getVecComponent("length")
    }

    /**
     *
     */
    override fun isConvertible(other: String): Boolean {
        val implicitConversions = GlslDefinitions.MATRICES[getTypeText()]
        return implicitConversions?.contains(other) ?: false
    }

    /**
     *
     */
    override fun getBinaryExprType(rightExprType: GlslType, expr: PsiElement?): GlslType? {
        if (rightExprType is GlslVector) {
            if (expr is GlslMulExpr) {
                val leftRows = getRowDimension()
                val leftColumn = getColumnDimension()
                val rightRows = rightExprType.getDimension()
                if (leftColumn == rightRows) {
                    return GlslVector("vec$leftRows")
                }
            }
            return null
        }
        return this
    }

    /**
     *
     */
    override fun getDimension(): Int {
        val lastChar = getTypeText().last()
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
    fun getColumnDimension(): Int {
        return getTypeText().last().digitToInt()
    }

    /**
     *
     */
    fun getRowDimension(): Int {
        val dims = dimensionsRegex.find(getTypeText())?.groupValues ?: return 0
        if (dims[2].isNotEmpty()) {
            return dims[2].toInt()
        } else if (dims[3].isNotEmpty()) {
            return dims[3].toInt()
        }
        return 0
    }

    /**
     *
     */
    private fun getMatrixComponentType(): String {
        val typeText = getTypeText().first()
        return when (typeText) {
            'm', 'f' -> "float"
            'd' -> "double"
            else -> ""
        }
    }

    /**
     *
     */
    fun getChildType(exprList: List<GlslExpr>): GlslType {
        if (exprList.isEmpty()) {
            return this
        }
        val componentType = getMatrixComponentType()
        if (exprList.size == 1) {
            val columnDimension = getColumnDimension()
            if (componentType == "float") {
                return GlslVector("vec$columnDimension")
            } else if (componentType == "double") {
                return GlslVector("dvec$columnDimension")
            }
        } else if (exprList.size == 2) {
            return GlslScalar(componentType)
        }
        return this
    }
}



