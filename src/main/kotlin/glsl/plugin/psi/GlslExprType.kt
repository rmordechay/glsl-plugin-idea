package glsl.plugin.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTFactory
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import glsl.GlslTypes
import glsl.plugin.psi.named.GlslNamedType
import glsl.plugin.psi.named.GlslNamedVariable
import glsl.plugin.utils.GlslUtils.getFuncCallType
import glsl.psi.impl.GlslBuiltinTypeScalarImpl
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
            is GlslRelationalExpr,
            is GlslEqualityExpr -> return getBooleanType()
            is GlslAddExpr,
            is GlslMulExpr,
            is GlslAndExpr,
            is GlslShiftExpr,
            is GlslExclusiveOrExpr,
            is GlslInclusiveOrExpr,
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
        return when (postfixExpr) {
            is GlslPrimaryExpr -> getPrimaryExprType(postfixExpr)
            is GlslFunctionCall -> getFuncCallType(postfixExpr)
            is GlslFieldSelection -> getPostfixType(postfixExpr.postfixExpr)
            is GlslPostfixInc -> getPostfixType(postfixExpr.postfixExpr)
            is GlslPostfixFieldSelection -> getPostfixSelectionType(postfixExpr)
            else -> null
        }
    }

    /**
     *
     */
    private fun getPrimaryExprType(postfixExpr: GlslPrimaryExpr): GlslNamedType? {
        if (postfixExpr.variableIdentifier != null) {
            val reference = postfixExpr.variableIdentifier?.reference?.resolve() ?: return null
            return (reference as? GlslNamedVariable)?.getAssociatedType()
        }

        val expr = postfixExpr.expr
        if (expr != null) {
            return expr.getExprType()
        }

        val node: ASTNode?
        if (postfixExpr.intconstant != null) {
            node = ASTFactory.leaf(GlslTypes.INT, "int")
        } else if (postfixExpr.uintconstant != null) {
            node = ASTFactory.leaf(GlslTypes.UINT, "uint")
        } else if (postfixExpr.boolconstant != null) {
            node = ASTFactory.leaf(GlslTypes.BOOL, "bool")
        } else if (postfixExpr.floatconstant != null) {
            node = ASTFactory.leaf(GlslTypes.FLOAT, "float")
        } else if (postfixExpr.stringLiteral != null) {
            node = ASTFactory.leaf(GlslTypes.STRING_LITERAL, postfixExpr.text)
        } else {
            return null
        }

        val builtinType = GlslBuiltinTypeScalarImpl(node)
        return builtinType
    }

    /**
     *
     */
    private fun getBinaryExprType(expr: PsiElement): GlslNamedType? {
        val exprList = PsiTreeUtil.getChildrenOfTypeAsList(expr, GlslExpr::class.java)
        val leftExpr = exprList.first().getExprType() ?: return null
        val rightExpr = exprList.last().getExprType() ?: return null
        val operation = expr.firstChild.nextSibling.nextSibling.text ?: ""
        return leftExpr.getBinaryType(rightExpr, operation)
    }

    /**
     *
     */
    private fun getBooleanType(): GlslBuiltinTypeScalarImpl {
        val node = ASTFactory.leaf(GlslTypes.BOOL, "bool")
        val builtinType = GlslBuiltinTypeScalarImpl(node)
        return builtinType
    }

    /**
     *
     */
    private fun getPostfixSelectionType(postfixExpr: GlslPostfixFieldSelection): GlslNamedType? {
        val variableIdentifiers = postfixExpr.postfixStructMemberList.mapNotNull { it.variableIdentifier }
        if (variableIdentifiers.isEmpty()) return null
        val lastExpr = variableIdentifiers.last() as? GlslVariable
        return lastExpr?.reference?.resolve()?.getAssociatedType()
    }
}
