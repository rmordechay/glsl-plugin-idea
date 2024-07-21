package glsl.plugin.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElementVisitor
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
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
            override fun visitSingleDeclaration(o: GlslSingleDeclaration) {
                val expr = o.exprNoAssignmentList.firstOrNull() ?: return
                val declarationType = o.getAssociatedType() ?: return
                val exprType = expr.getExprType() ?: return
                if (exprType.errorMessage != null) {
                    holder.registerProblem(expr, exprType.errorMessage!!, ProblemHighlightType.GENERIC_ERROR)
                    return
                }
                if (declarationType.isEqual(exprType)) return
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
            override fun visitFunctionCall(o: GlslFunctionCall) {
                val constructorIdentifier = o.typeSpecifier?.typeName
                if (constructorIdentifier != null) {
                    val structSpecifier = constructorIdentifier.reference?.resolve() as? GlslStructSpecifier ?: return
                    val expectedParamCount = structSpecifier.getStructMembers().size
                    val actualParamsExprs = o.exprNoAssignmentList
                    if (expectedParamCount > actualParamsExprs.size) {
                        val msg = TOO_FEW_ARGUMENTS_CONSTRUCTOR.format(constructorIdentifier.getName())
                        val textRange = TextRange(o.leftParen.startOffset, o.rightParen.endOffset)
                        holder.registerProblem(o, msg, ProblemHighlightType.GENERIC_ERROR, textRange)
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
            override fun visitFunctionCall(o: GlslFunctionCall) {
                val constructorIdentifier = o.typeSpecifier?.typeName ?: return
                val structSpecifier = constructorIdentifier.reference?.resolve() as? GlslStructSpecifier ?: return
                val expectedParamCount = structSpecifier.getStructMembers().size
                val actualParamsExprs = o.exprNoAssignmentList
                if (expectedParamCount < actualParamsExprs.size) {
                    val msg = TOO_MANY_ARGUMENTS_CONSTRUCTOR.format(constructorIdentifier.getName())
                    holder.registerProblem(o, msg, ProblemHighlightType.GENERIC_ERROR)
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
            override fun visitFunctionCall(o: GlslFunctionCall) {
                val funcIdentifier = o.variableIdentifier ?: return
                val functionDeclarator = funcIdentifier.reference?.resolve() as? GlslFunctionDeclaratorImpl ?: return
                val paramTypes = functionDeclarator.getParameterTypes() ?: return
                val exprTypes = o.exprNoAssignmentList.mapNotNull { it.getExprType() }
                if (paramTypes.size != exprTypes.size) {
                    val actualTypesString = exprTypes.mapNotNull { it.name }.joinToString(", ")
                    val msg = NO_MATCHING_FUNCTION_CALL.format(funcIdentifier.getName(), actualTypesString)
                    holder.registerProblem(o, msg, ProblemHighlightType.GENERIC_ERROR)
                }
            }
        }
    }
}


