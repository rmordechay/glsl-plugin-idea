package glsl.plugin.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType.GENERIC_ERROR
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import glsl.data.GlslErrorMessages.Companion.INCOMPATIBLE_TYPES_IN_INIT
import glsl.plugin.psi.named.GlslNamedType
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
                var expr = singleDeclaration.exprNoAssignmentList.firstOrNull()
                if (expr == null) {
                    expr = (singleDeclaration.parent as GlslDeclaration).exprNoAssignmentList.firstOrNull() ?: return
                }
                val declarationType = singleDeclaration.getAssociatedType() ?: return
                val exprType = expr.getExprType() ?: return
                if (declarationType.isEqual(exprType)) return
                holder.registerProblem(expr, INCOMPATIBLE_TYPES_IN_INIT, GENERIC_ERROR)
            }

        }
    }
}