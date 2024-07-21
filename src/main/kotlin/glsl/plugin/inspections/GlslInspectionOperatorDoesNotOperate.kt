package glsl.plugin.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import glsl.psi.interfaces.GlslSingleDeclaration
import glsl.psi.interfaces.GlslVisitor


/**
 *
 */
class GlslInspectionOperatorDoesNotOperate : LocalInspectionTool() {
    /**
     *
     */
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : GlslVisitor() {
            override fun visitSingleDeclaration(singleDeclaration: GlslSingleDeclaration) {
                val expr = singleDeclaration.exprNoAssignmentList.firstOrNull() ?: return
                val exprType = expr.getExprType()
                if (exprType?.errorMessage == null) return
                holder.registerProblem(expr, exprType.errorMessage!!, ProblemHighlightType.GENERIC_ERROR)
            }
        }
    }
}

