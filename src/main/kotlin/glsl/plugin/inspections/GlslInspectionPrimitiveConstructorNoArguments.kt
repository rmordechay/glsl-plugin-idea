package glsl.plugin.inspections

import com.intellij.codeInspection.ProblemHighlightType.GENERIC_ERROR
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import glsl.data.GlslErrorCode
import glsl.plugin.utils.GlslUtils.getType
import glsl.psi.interfaces.GlslFunctionCall
import glsl.psi.interfaces.GlslVisitor

/**
 *
 */
class GlslInspectionPrimitiveConstructorNoArguments : GlslInspection() {
    override val errorMessageCode = GlslErrorCode.PRIMITIVE_CONSTRUCTOR_ZERO_ARGUMENTS

    /**
     *
     */
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : GlslVisitor() {
            override fun visitFunctionCall(functionCall: GlslFunctionCall) {
                val typeSpecifier = functionCall.typeSpecifier ?: return
                val type = getType(typeSpecifier) ?: return
                val args = functionCall.exprNoAssignmentList
                if (type.isPrimitive && args.isEmpty()) {
                    holder.registerProblem(functionCall, errorMessageCode.message, GENERIC_ERROR)
                }
            }
        }
    }
}