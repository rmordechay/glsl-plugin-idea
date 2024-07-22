package glsl.plugin.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import glsl.data.GlslErrorCode
import glsl.psi.impl.GlslFunctionDeclaratorImpl
import glsl.psi.interfaces.GlslFunctionCall
import glsl.psi.interfaces.GlslVisitor

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
                val exprTypes = functionCall.exprNoAssignmentList.map { it.getExprType() }
                if (paramTypes.size == exprTypes.size || exprTypes.any { it == null }) return

                val actualTypesString = exprTypes.mapNotNull { it?.name }.joinToString(", ")
                val msg = GlslErrorCode.NO_MATCHING_FUNCTION_CALL.message.format(funcIdentifier.getName(), actualTypesString)
                holder.registerProblem(functionCall, msg, ProblemHighlightType.GENERIC_ERROR)
            }
        }
    }
}