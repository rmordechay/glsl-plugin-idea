package glsl.plugin.inspections

import com.intellij.codeInspection.ProblemHighlightType.GENERIC_ERROR
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import glsl.plugin.utils.GlslUtils.getType
import glsl.psi.interfaces.GlslConstructorCall
import glsl.psi.interfaces.GlslVisitor

/**
 *
 */
class GlslInspectionConstructorNoArguments : GlslInspection() {
    override val errorMessageCode = GlslErrorCode.PRIMITIVE_CONSTRUCTOR_ZERO_ARGUMENTS

    /**
     *
     */
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : GlslVisitor() {
            override fun visitConstructorCall(constructorCall: GlslConstructorCall) {
                val type = getType(constructorCall.typeSpecifier) ?: return
                val args = constructorCall.exprNoAssignmentList
                if (type.isPrimitive && args.isEmpty()) {
                    holder.registerProblem(constructorCall, errorMessageCode.message, GENERIC_ERROR)
                }
            }
        }
    }
}