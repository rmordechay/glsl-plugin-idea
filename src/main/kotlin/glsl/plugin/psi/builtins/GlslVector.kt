package glsl.plugin.psi.builtins

import com.intellij.psi.PsiElement
import glsl.data.GlslDefinitions
import glsl.plugin.psi.GlslType
import glsl.plugin.psi.named.GlslNamedElement
import glsl.plugin.utils.GlslBuiltinUtils
import glsl.psi.interfaces.GlslExpr
import glsl.psi.interfaces.GlslMulExpr


/**
 *
 */
class GlslVector(private val typeText: String) : GlslType {

    /**
     *
     */
    override fun getStructMembers(): List<GlslNamedElement> {
        val vectorComponents = GlslBuiltinUtils.getVecStructs()[getTypeText()]
        return vectorComponents?.values?.toList() ?: return emptyList()
    }

    /**
     *
     */
    override fun getStructMember(memberName: String): GlslNamedElement? {
        return GlslBuiltinUtils.getVecComponent(memberName, getTypeText())
    }

    /**
     *
     */
    override fun getTypeText(): String {
        return typeText
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
    private fun getVectorComponentType(): String {
        val typeText = getTypeText().first()
        return when (typeText) {
            'v', 'f' -> "float"
            'd' -> "double"
            'u' -> "uint"
            'i' -> "int"
            'b' -> "bool"
            else -> ""
        }
    }

    /**
     *
     */
    override fun isConvertible(other: String): Boolean {
        val implicitConversions = GlslDefinitions.VECTORS[getTypeText()]
        return implicitConversions?.contains(other) ?: false
    }

    /**
     *
     */
    override fun getBinaryExprType(rightExprType: GlslType, expr: PsiElement?): GlslType? {
        if (rightExprType is GlslScalar || rightExprType is GlslVector) {
            return this
        } else if (rightExprType is GlslMatrix) {
            if (expr is GlslMulExpr) {
                val leftRows = getDimension()
                val leftColumn = 1
                val rightRows = rightExprType.getRowDimension()
                val rightColumns = rightExprType.getColumnDimension()
                if (leftColumn == rightRows) {
                    if (leftRows == rightColumns) {
                        return GlslVector("mat{$leftRows}")
                    } else {
                        return GlslVector("mat{$leftRows}x{$rightColumns}")
                    }
                }
            }
            return null
        }
        return this
    }

    /**
     *
     */
    fun getChildType(exprList: List<GlslExpr>): GlslType {
        if (exprList.isEmpty()) {
            return this
        }
        return GlslScalar(getVectorComponentType())
    }
}
