package glsl.plugin.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTFactory.leaf
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import glsl.GlslTypes.*
import glsl.plugin.psi.named.GlslNamedType
import glsl.plugin.utils.GlslUtils.createScalarTypeElement
import glsl.plugin.utils.GlslUtils.getType
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
            is GlslFunctionCall -> postfixExpr.variableIdentifier?.resolveReference()?.getAssociatedType()
            is GlslConstructorCall -> getType(postfixExpr.typeSpecifier)
            is GlslPostfixArrayIndex -> getArrayIndexType(postfixExpr)
            is GlslPostfixInc -> getPostfixType(postfixExpr.postfixExpr)
            is GlslPostfixFieldSelection -> getPostfixSelectionType(postfixExpr)
            else -> null
        }
    }

    /**
     *
     */
    private fun getArrayIndexType(arrayIndex: GlslPostfixArrayIndex): GlslNamedType? {
        val variableIdentifier = (arrayIndex.postfixExpr as GlslPrimaryExpr).variableIdentifier
        val resolvedReference = variableIdentifier?.resolveReference()
        return resolvedReference?.getAssociatedType()?.getScalarType()
    }

    /**
     *
     */
    private fun getPrimaryExprType(primaryExpr: GlslPrimaryExpr): GlslNamedType? {
        if (primaryExpr.variableIdentifier != null) {
            val reference = primaryExpr.variableIdentifier?.resolveReference() ?: return null
            return reference.getAssociatedType()
        }

        val expr = primaryExpr.expr
        if (expr != null) {
            return expr.getExprType()
        }
        if (primaryExpr.intconstant != null) {
            return createScalarTypeElement(INT, "int")
        } else if (primaryExpr.uintconstant != null) {
            return createScalarTypeElement(UINT, "uint")
        } else if (primaryExpr.boolconstant != null) {
            return createScalarTypeElement(BOOL, "bool")
        } else if (primaryExpr.floatconstant != null) {
            return createScalarTypeElement(FLOAT, "float")
        } else {
            return null
        }
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
        val node = leaf(BOOL, "bool")
        val builtinType = GlslBuiltinTypeScalarImpl(node)
        return builtinType
    }

    /**
     *
     */
    private fun getPostfixSelectionType(`postfixExpr`: GlslPostfixFieldSelection): GlslNamedType? {
        if (postfixExpr.postfixStructMemberList.isEmpty()) return null
        val lastExpr = postfixExpr.postfixStructMemberList.last()
        if (lastExpr.variableIdentifier == null)
            return null
        val type = lastExpr.variableIdentifier?.resolveReference()?.getAssociatedType()
        if (lastExpr.expr != null) {
            return type?.getScalarType()
        }
        return type
    }
}
