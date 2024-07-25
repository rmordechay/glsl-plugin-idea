package glsl.plugin.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import glsl.psi.impl.GlslFunctionDeclaratorImpl
import glsl.psi.interfaces.GlslFunctionCall
import glsl.psi.interfaces.GlslVisitor

/**
 *
 */
class GlslInspectionNoMatchingFunction : GlslInspection() {
    override val errorMessageCode = GlslErrorCode.NO_MATCHING_FUNCTION_CALL

    /**
     *
     */
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : GlslVisitor() {
            override fun visitFunctionCall(functionCall: GlslFunctionCall) {
                val funcIdentifier = functionCall.variableIdentifier ?: return
                val functionDeclarators = funcIdentifier.reference?.resolveMany() ?: return
                val exprTypes = functionCall.exprNoAssignmentList.map { it.getExprType() }
                for (functionDeclarator in functionDeclarators) {
                    if (functionDeclarator !is GlslFunctionDeclaratorImpl) continue
                    val paramTypes = functionDeclarator.getParameterTypes() ?: continue
                    if (paramTypes.size == exprTypes.size) continue
                    val argsMatch = paramTypes.zip(exprTypes).all { it.first.isEqual(it.second)}
                    if (argsMatch) return
                }
                val actualTypesString = exprTypes.mapNotNull { it?.name }.joinToString(", ")
                val msg = errorMessageCode.message.format(funcIdentifier.getName(), actualTypesString)
                holder.registerProblem(functionCall, msg, ProblemHighlightType.GENERIC_ERROR)
            }
        }
    }
}