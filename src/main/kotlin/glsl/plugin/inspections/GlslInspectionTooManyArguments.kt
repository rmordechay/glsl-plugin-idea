package glsl.plugin.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import glsl.data.GlslErrorCode
import glsl.psi.interfaces.GlslFunctionCall
import glsl.psi.interfaces.GlslStructSpecifier
import glsl.psi.interfaces.GlslVisitor

/**
 *
 */
class GlslInspectionTooManyArguments : LocalInspectionTool() {
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
                if (expectedParamCount < actualParamsExprs.size) {
                    val msg = GlslErrorCode.TOO_MANY_ARGUMENTS_CONSTRUCTOR.message.format(constructorIdentifier.getName())
                    holder.registerProblem(functionCall, msg, ProblemHighlightType.GENERIC_ERROR)
                }
            }
        }
    }
}