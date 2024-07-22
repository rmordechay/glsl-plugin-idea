package glsl.plugin.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType.GENERIC_ERROR
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import glsl.data.GlslErrorCode
import glsl.psi.interfaces.GlslDeclaration
import glsl.psi.interfaces.GlslSingleDeclaration
import glsl.psi.interfaces.GlslVisitor

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
                var expr = singleDeclaration.exprNoAssignmentList.firstOrNull()
                if (expr == null) {
                    expr = (singleDeclaration.parent as GlslDeclaration).exprNoAssignmentList.firstOrNull() ?: return
                }
                val declarationType = singleDeclaration.getAssociatedType() ?: return
                val exprType = expr.getExprType() ?: return
                if (declarationType.isEqual(exprType)) return
                holder.registerProblem(expr, GlslErrorCode.INCOMPATIBLE_TYPES_IN_INIT.message, GENERIC_ERROR)
            }

        }
    }
}