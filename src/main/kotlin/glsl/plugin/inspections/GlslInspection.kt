package glsl.plugin.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.PsiTreeUtil.collectElements
import com.intellij.psi.util.elementType
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import glsl.GlslTypes.RETURN
import glsl.data.GlslErrorMessages.Companion.INCOMPATIBLE_TYPES_IN_INIT
import glsl.data.GlslErrorMessages.Companion.MISSING_RETURN_FUNCTION
import glsl.data.GlslErrorMessages.Companion.NO_MATCHING_FUNCTION_CALL
import glsl.data.GlslErrorMessages.Companion.TOO_FEW_ARGUMENTS_CONSTRUCTOR
import glsl.data.GlslErrorMessages.Companion.TOO_MANY_ARGUMENTS_CONSTRUCTOR
import glsl.psi.impl.GlslFunctionDeclaratorImpl
import glsl.psi.interfaces.*


/**
 *
 */
class GlslInspectionIncompatibleType : LocalInspectionTool() {
    /**
     *
     */
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : GlslVisitor() {
            override fun visitSingleDeclaration(singleDeclaration: GlslSingleDeclaration) {
                val expr = singleDeclaration.exprNoAssignmentList.firstOrNull() ?: return
                val declarationType = singleDeclaration.getAssociatedType() ?: return
                val exprType = expr.getExprType() ?: return
                if (exprType.errorMessage != null) {
                    holder.registerProblem(expr, exprType.errorMessage!!, ProblemHighlightType.GENERIC_ERROR)
                    return
                }
                if (declarationType.isEqual(exprType)) return
                holder.registerProblem(expr, INCOMPATIBLE_TYPES_IN_INIT, ProblemHighlightType.GENERIC_ERROR)
            }
        }
    }
}

/**
 *
 */
class GlslInspectionTooFewArguments : LocalInspectionTool() {
    /**
     *
     */
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object: GlslVisitor() {
            override fun visitFunctionCall(functionCall: GlslFunctionCall) {
                val constructorIdentifier = functionCall.typeSpecifier?.typeName
                if (constructorIdentifier != null) {
                    val structSpecifier = constructorIdentifier.reference?.resolve() as? GlslStructSpecifier ?: return
                    val expectedParamCount = structSpecifier.getStructMembers().size
                    val actualParamsExprs = functionCall.exprNoAssignmentList
                    if (expectedParamCount > actualParamsExprs.size) {
                        val msg = TOO_FEW_ARGUMENTS_CONSTRUCTOR.format(constructorIdentifier.getName())
                        val textRange = TextRange(functionCall.leftParen.startOffset, functionCall.rightParen.endOffset)
                        holder.registerProblem(functionCall, msg, ProblemHighlightType.GENERIC_ERROR, textRange)
                    }
                }
            }
        }
    }
}

/**
 *
 */
class GlslInspectionTooManyArguments : LocalInspectionTool() {
    /**
     *
     */
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object: GlslVisitor() {
            override fun visitFunctionCall(functionCall: GlslFunctionCall) {
                val constructorIdentifier = functionCall.typeSpecifier?.typeName ?: return
                val structSpecifier = constructorIdentifier.reference?.resolve() as? GlslStructSpecifier ?: return
                val expectedParamCount = structSpecifier.getStructMembers().size
                val actualParamsExprs = functionCall.exprNoAssignmentList
                if (expectedParamCount < actualParamsExprs.size) {
                    val msg = TOO_MANY_ARGUMENTS_CONSTRUCTOR.format(constructorIdentifier.getName())
                    holder.registerProblem(functionCall, msg, ProblemHighlightType.GENERIC_ERROR)
                }
            }
        }
    }
}

/**
 *
 */
class GlslInspectionNoMatchingFunction : LocalInspectionTool() {
    /**
     *
     */
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : GlslVisitor() {
            override fun visitFunctionCall(functionCall: GlslFunctionCall) {
                val funcIdentifier = functionCall.variableIdentifier ?: return
                val functionDeclarator = funcIdentifier.reference?.resolve() as? GlslFunctionDeclaratorImpl ?: return
                val paramTypes = functionDeclarator.getParameterTypes() ?: return
                val exprTypes = functionCall.exprNoAssignmentList.mapNotNull { it.getExprType() }
                if (paramTypes.size != exprTypes.size) {
                    val actualTypesString = exprTypes.mapNotNull { it.name }.joinToString(", ")
                    val msg = NO_MATCHING_FUNCTION_CALL.format(funcIdentifier.getName(), actualTypesString)
                    holder.registerProblem(functionCall, msg, ProblemHighlightType.GENERIC_ERROR)
                }
            }
        }
    }
}

/**
 *
 */
class GlslInspectionMissingReturn : LocalInspectionTool() {
    /**
     *
     */
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object: GlslVisitor() {
            override fun visitFunctionDefinition(functionDefinition: GlslFunctionDefinition) {
                val functionDeclarator = functionDefinition.functionDeclarator
                if (functionDeclarator.typeSpecifier.textMatches("void")) return
                val returnExists = collectElements(functionDefinition) { e -> e.elementType == RETURN }.isNotEmpty()
                if (returnExists) return
                val textRange = TextRange(functionDefinition.endOffset - 1, functionDefinition.endOffset)
                val funcName = functionDeclarator.name
                val msg = MISSING_RETURN_FUNCTION.format(funcName)
                holder.registerProblem(functionDefinition, msg, ProblemHighlightType.GENERIC_ERROR, textRange)
            }
        }
    }
}

