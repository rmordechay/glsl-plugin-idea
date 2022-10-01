package glsl.plugin.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import glsl.plugin.psi.builtins.GlslMatrix
import glsl.plugin.psi.builtins.GlslScalar
import glsl.psi.interfaces.*

interface GlslExprType : PsiElement {
    fun getExprType(): GlslType?
}

abstract class GlslExprTypeImpl(node: ASTNode) : ASTWrapperPsiElement(node), GlslExprType {

    /**
     *
     */
    override fun getExprType(): GlslType? {
        val expr = node.psi ?: return null
        when (expr) {
            is GlslUnaryExpr -> return getPostfixType(expr.postfixExpr)
            is GlslConditionalExpr -> return expr.exprNoAssignmentList[1].getExprType()
            is GlslRelationalExpr,
            is GlslEqualityExpr -> return GlslScalar("bool")
            is GlslMulExpr,
            is GlslAddExpr,
            is GlslAndExpr,
            is GlslExclusiveOrExpr,
            is GlslInclusiveOrExpr,
            is GlslShiftExpr,
            is GlslLogicalAndExpr,
            is GlslLogicalXorExpr,
            is GlslLogicalOrExpr -> {
                val exprList = PsiTreeUtil.getChildrenOfTypeAsList(expr, GlslExpr::class.java)
                val leftExpr = exprList.first().getExprType()
                val rightExpr = exprList.last().getExprType() ?: return null
                return leftExpr?.getBinaryExprType(rightExpr, expr)
            }
        }
        return null
    }

    /**
     *
     */
    private fun getPostfixType(postfixExpr: GlslPostfixExpr?): GlslType? {
        when (postfixExpr) {
            is GlslPrimaryExpr -> {
                if (postfixExpr.variableIdentifier != null) {
                    return postfixExpr.variableIdentifier?.reference?.resolve()?.getAssociatedType()
                } else if (postfixExpr.expr != null) {
                    return postfixExpr.expr!!.getExprType()
                } else {
                    if (postfixExpr.intconstant != null) return GlslScalar("int")
                    else if (postfixExpr.uintconstant != null) return GlslScalar("uint")
                    else if (postfixExpr.boolconstant != null) return GlslScalar("bool")
                    else if (postfixExpr.floatconstant != null) return GlslScalar("float")
                    else if (postfixExpr.stringLiteral != null) return GlslScalar("string")
                }
            }
            is GlslFunctionCall -> {
                if (postfixExpr.variableIdentifier != null) {
                    return postfixExpr.variableIdentifier?.reference?.resolve()?.getAssociatedType()
                } else if (postfixExpr.typeSpecifier != null) {
                    return GlslType.getInstance(postfixExpr.typeSpecifier)
                }
            }
            is GlslPostfixFieldSelection -> {
                val lastExpr = postfixExpr.postfixStructMemberList.map { it.variableIdentifier }.last()
                return lastExpr?.reference?.resolve()?.getAssociatedType()
            }
            is GlslPostfixArrayIndex -> {
                val postfixType = getPostfixType(postfixExpr.postfixExpr)
                if (postfixType is GlslMatrix) {
                    return postfixType.getChildType(postfixExpr.exprList)
                }
                return getPostfixType(postfixExpr.postfixExpr)
            }
            is GlslPostfixInc -> return getPostfixType(postfixExpr.postfixExpr)
        }
        return null
    }
}