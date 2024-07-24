package glsl.plugin.inspections

import com.intellij.codeInspection.ProblemHighlightType.GENERIC_ERROR
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElementVisitor
import glsl.plugin.utils.GlslUtils.getType
import glsl.psi.interfaces.GlslConstructorCall
import glsl.psi.interfaces.GlslVisitor

/**
 *
 */
class GlslInspectionTooFewArguments : GlslInspection() {
    override val errorMessageCode = GlslErrorCode.TOO_FEW_ARGUMENTS_CONSTRUCTOR

    /**
     *
     */
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : GlslVisitor() {
            override fun visitConstructorCall(constructorCall: GlslConstructorCall) {
                val constructorType = getType(constructorCall.typeSpecifier) ?: return
                if (constructorType.isPrimitive) return
                val actualParamsCount = constructorCall.exprNoAssignmentList.size
                val expectedParamCount = constructorType.getStructMembers().size
                if (expectedParamCount <= actualParamsCount) return
                val msg = errorMessageCode.message.format(constructorType.name)
                val startOffset = constructorCall.leftParen.textRangeInParent.startOffset
                val endOffset = constructorCall.rightParen.textRangeInParent.endOffset
                holder.registerProblem(constructorCall, msg, GENERIC_ERROR, TextRange(startOffset, endOffset))
            }
        }
    }
}