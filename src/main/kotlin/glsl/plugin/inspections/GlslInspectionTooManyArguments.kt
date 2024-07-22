package glsl.plugin.inspections

import com.intellij.codeInspection.ProblemHighlightType.GENERIC_ERROR
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElementVisitor
import glsl.data.GlslErrorCode
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
            override fun visitFunctionCall(functionCall: GlslFunctionCall) {
                val constructorIdentifier = functionCall.typeSpecifier?.typeName ?: return
                val structSpecifier = constructorIdentifier.reference?.resolve() as? GlslStructSpecifier ?: return
                val expectedParamCount = structSpecifier.getStructMembers().size
                val actualParamsExprs = functionCall.exprNoAssignmentList
                if (expectedParamCount >= actualParamsExprs.size) return
                val msg = errorMessageCode.message.format(constructorIdentifier.getName())
                val startOffset = functionCall.leftParen.textRange.startOffset
                val endOffset = functionCall.rightParen.textRange.endOffset
                holder.registerProblem(functionCall, msg, GENERIC_ERROR, TextRange(startOffset, endOffset))
            }
        }
    }
}