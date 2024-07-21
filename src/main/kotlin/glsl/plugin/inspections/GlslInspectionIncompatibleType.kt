package glsl.plugin.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import glsl.data.GlslErrorMessages
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
                val expr = singleDeclaration.exprNoAssignmentList.firstOrNull() ?: return
                val declarationType = singleDeclaration.getAssociatedType() ?: return
                val exprType = expr.getExprType() ?: return
                if (declarationType.isEqual(exprType)) return
                holder.registerProblem(expr,
                    GlslErrorMessages.INCOMPATIBLE_TYPES_IN_INIT,
                    ProblemHighlightType.GENERIC_ERROR
                )
            }
        }
    }
}