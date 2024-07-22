package glsl.plugin.inspections

import com.intellij.codeInspection.ProblemHighlightType.GENERIC_ERROR
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElementVisitor
import glsl.plugin.utils.GlslUtils.getType
import glsl.psi.interfaces.GlslConstructorCall
import glsl.psi.interfaces.GlslFunctionCall
import glsl.psi.interfaces.GlslStructSpecifier
import glsl.psi.interfaces.GlslVisitor

/**
 *
 */
class GlslInspectionTooManyArguments : GlslInspection() {
    override val errorMessageCode = GlslErrorCode.TOO_MANY_ARGUMENTS_CONSTRUCTOR

    /**
     *
     */
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object: GlslVisitor() {
            override fun visitConstructorCall(constructorCall: GlslConstructorCall) {
                val structSpecifier = getType(constructorCall.typeSpecifier) as? GlslStructSpecifier ?: return
                val expectedParamCount = structSpecifier.getStructMembers().size
                val actualParamsExprs = constructorCall.exprNoAssignmentList
                if (expectedParamCount >= actualParamsExprs.size) return
                val msg = errorMessageCode.message.format(structSpecifier.name)
                val startOffset = constructorCall.leftParen.textRange.startOffset
                val endOffset = constructorCall.rightParen.textRange.endOffset
                holder.registerProblem(constructorCall, msg, GENERIC_ERROR, TextRange(startOffset, endOffset))
            }
        }
    }
}