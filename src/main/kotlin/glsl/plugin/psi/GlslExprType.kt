package glsl.plugin.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import glsl.plugin.psi.builtins.GlslScalar
import glsl.plugin.psi.named.GlslNamedType
import glsl.plugin.psi.named.GlslNamedTypeImpl
import glsl.plugin.psi.named.GlslNamedVariable
import glsl.psi.interfaces.*

interface GlslExprType : PsiElement {
    fun getExprType(): GlslNamedType?
}

abstract class GlslExprTypeImpl(node: ASTNode) : ASTWrapperPsiElement(node), GlslExprType {

    /**
     *
     */
    override fun getExprType(): GlslNamedType? {
        val expr = node.psi ?: return null
        when (expr) {
            is GlslUnaryExpr -> return getPostfixType(expr.postfixExpr)
            is GlslConditionalExpr -> return expr.exprNoAssignmentList.getOrNull(1)?.getExprType()
//            is GlslRelationalExpr,
//            is GlslEqualityExpr -> return GlslScalar("bool")
            is GlslMulExpr,
            is GlslAddExpr,
            is GlslAndExpr,
            is GlslExclusiveOrExpr,
            is GlslInclusiveOrExpr,
            is GlslShiftExpr,
            is GlslLogicalAndExpr,
            is GlslLogicalXorExpr,
            is GlslLogicalOrExpr -> return getBinaryExprType(expr)
        }
        return null
    }

    /**
     *
     */
    private fun getPostfixType(postfixExpr: GlslPostfixExpr?): GlslNamedType? {
        when (postfixExpr) {
            is GlslPrimaryExpr -> {
                getPrimaryExprType(postfixExpr)
            }
            is GlslFunctionCall -> {
                getFunctionCallType(postfixExpr)
            }
            is GlslPostfixFieldSelection -> {
                val lastExpr = postfixExpr.postfixStructMemberList.map { it.variableIdentifier }.last()
                return lastExpr?.reference?.resolve()?.getAssociatedType()
            }
            is GlslPostfixArrayIndex -> {
                return getPostfixArrayType(postfixExpr)
            }
            is GlslPostfixInc -> return getPostfixType(postfixExpr.postfixExpr)
        }
        return null
    }

    /**
     *
     */
    private fun getPostfixArrayType(postfixExpr: GlslPostfixArrayIndex): GlslNamedType? {
        val postfixType = getPostfixType(postfixExpr.postfixExpr)
//        if (postfixType is GlslMatrix) {
//            return postfixType.getChildType(postfixExpr.exprList)
//        } else if (postfixType is GlslVector) {
//            return postfixType.getChildType(postfixExpr.exprList)
//        }
        return postfixType
    }

    /**
     *
     */
    private fun getFunctionCallType(postfixExpr: GlslFunctionCall): GlslNamedType? {
        if (postfixExpr.variableIdentifier != null) {
            return postfixExpr.variableIdentifier?.reference?.resolve()?.getAssociatedType()
        } else if (postfixExpr.typeSpecifier != null) {
            return null
        }
        return null
    }

    /**
     *
     */
    private fun getBinaryExprType(expr: PsiElement): GlslNamedType? {
        val exprList = PsiTreeUtil.getChildrenOfTypeAsList(expr, GlslExpr::class.java)
        val leftExpr = exprList.first().getExprType()
        val rightExpr = exprList.last().getExprType() ?: return null
        return null
    }

    /**
     *
     */
    private fun getPrimaryExprType(postfixExpr: GlslPrimaryExpr): GlslNamedType? {
        if (postfixExpr.variableIdentifier != null) {
//            return postfixExpr.variableIdentifier?.reference?.resolve()?.getAssociatedType()
        } else if (postfixExpr.expr != null) {
//            return postfixExpr.expr!!.getExprType()
        } else {
//            if (postfixExpr.intconstant != null) return GlslScalar("int")
//            else if (postfixExpr.uintconstant != null) return GlslScalar("uint")
//            else if (postfixExpr.boolconstant != null) return GlslScalar("bool")
//            else if (postfixExpr.floatconstant != null) return GlslScalar("float")
//            else if (postfixExpr.stringLiteral != null) return GlslScalar("string")
        }
        return null
    }
}