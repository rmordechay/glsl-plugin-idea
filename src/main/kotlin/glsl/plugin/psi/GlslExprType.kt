package glsl.plugin.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import glsl.plugin.psi.named.GlslNamedType
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
                return getPrimaryExprType(postfixExpr)
            }
            is GlslFunctionCall -> {
                return getFunctionCallType(postfixExpr)
            }
            is GlslPostfixFieldSelection -> {
//                val variableIdentifiers = postfixExpr.postfixStructMemberList.mapNotNull { it.variableIdentifier }
//                if (variableIdentifiers.isEmpty()) return null
//                val lastExpr = variableIdentifiers.last() as? GlslVariable
//                return lastExpr?.reference?.resolve()?.getAssociatedType()
            }
            is GlslFieldSelection -> {
                return getFieldSelection(postfixExpr)
            }
            is GlslPostfixInc -> {
                return getPostfixType(postfixExpr.postfixExpr)
            }
        }
        return null
    }

    /**
     *
     */
    private fun getFieldSelection(fieldSelection: GlslFieldSelection): GlslNamedType? {
        val postfixType = getPostfixType(fieldSelection.postfixExpr)
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
    private fun getFunctionCallType(functionCall: GlslFunctionCall): GlslNamedType? {
        if (functionCall.variableIdentifier != null) {
            val reference = functionCall.variableIdentifier?.reference?.resolve() ?: return null
            return (reference as? GlslNamedVariable)?.getAssociatedType()
        } else if (functionCall.typeSpecifier?.typeName != null) {
            val reference = functionCall.typeSpecifier?.typeName?.reference?.resolve() ?: return null
            return reference as? GlslNamedType
        }
        return null
    }

    /**
     *
     */
    private fun getBinaryExprType(expr: PsiElement): GlslNamedType? {
//        val exprList = PsiTreeUtil.getChildrenOfTypeAsList(expr, GlslExpr::class.java)
//        val leftExpr = exprList.first().getExprType()
//        val rightExpr = exprList.last().getExprType() ?: return null
        return null
    }

    /**
     *
     */
    private fun getPrimaryExprType(postfixExpr: GlslPrimaryExpr): GlslNamedType? {
        if (postfixExpr.variableIdentifier != null) {
            val reference = postfixExpr.variableIdentifier?.reference?.resolve() ?: return null
            return (reference as? GlslNamedVariable)?.getAssociatedType()
        } else if (postfixExpr.expr != null) {
            return postfixExpr.expr!!.getExprType()
        } else {
            if (postfixExpr.intconstant != null) return null
            else if (postfixExpr.uintconstant != null) return null
            else if (postfixExpr.boolconstant != null) return null
            else if (postfixExpr.floatconstant != null) return null
            else if (postfixExpr.stringLiteral != null) return null
        }
        return null
    }
}