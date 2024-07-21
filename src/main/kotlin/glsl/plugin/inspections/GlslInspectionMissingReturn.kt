package glsl.plugin.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import glsl.GlslTypes
import glsl.data.GlslErrorMessages
import glsl.psi.interfaces.GlslFunctionDefinition
import glsl.psi.interfaces.GlslVisitor

/**
 *
 */
class GlslInspectionMissingReturn : LocalInspectionTool() {
    /**
     *
     */
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object: GlslVisitor() {
            override fun visitFunctionDefinition(functionDefinition: GlslFunctionDefinition) {
                val functionDeclarator = functionDefinition.functionDeclarator
                if (functionDeclarator.typeSpecifier.textMatches("void")) return
                val returnExists = PsiTreeUtil.collectElements(functionDefinition) { e -> e.elementType == GlslTypes.RETURN }
                    .isNotEmpty()
                if (returnExists) return
                val endOffset = functionDefinition.textRangeInParent.endOffset
                val textRange = TextRange(endOffset - 1, endOffset)
                val funcName = functionDeclarator.name
                val msg = GlslErrorMessages.MISSING_RETURN_FUNCTION.format(funcName)
                holder.registerProblem(functionDefinition, msg, ProblemHighlightType.GENERIC_ERROR, textRange)
            }
        }
    }
}