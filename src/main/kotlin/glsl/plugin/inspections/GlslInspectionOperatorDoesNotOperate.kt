package glsl.plugin.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import glsl.data.GlslErrorCode
import glsl.psi.interfaces.GlslSingleDeclaration
import glsl.psi.interfaces.GlslVisitor


/**
 *
 */
class GlslInspectionOperatorDoesNotOperate : GlslInspection() {
    override val errorMessageCode = GlslErrorCode.DOES_NOT_OPERATE_ON

    /**
     *
     */
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : GlslVisitor() {
            override fun visitSingleDeclaration(singleDeclaration: GlslSingleDeclaration) {
                val expr = singleDeclaration.exprNoAssignmentList.firstOrNull() ?: return
                val exprType = expr.getExprType()
                val error = exprType?.glslError ?: return
                if (error.errorCode != errorMessageCode) return
                holder.registerProblem(expr, error.formattedMessage, ProblemHighlightType.GENERIC_ERROR)
            }
        }
    }
}

