package glsl.plugin.annotator

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import glsl.plugin.psi.named.GlslNamedFunctionHeader
import glsl.psi.interfaces.GlslFunctionCall
import glsl.psi.interfaces.GlslSingleDeclaration
import glsl.psi.interfaces.GlslVariableIdentifier


class GlslCodeAnnotator : Annotator {

    /**
     *
     */
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        when (element) {
            is GlslSingleDeclaration -> {

            }
            is GlslFunctionCall -> {
                annotateFunctionCall(element, holder)
            }
        }
    }

    /**
     *
     */
    private fun annotateIncorrectParamCount(element: GlslFunctionCall, holder: AnnotationHolder) {
        val funcReference = element.variableIdentifier?.reference ?: return
        funcReference.resolve()
        if (funcReference.resolvedReferences.isEmpty()) return
        val actualParamsExprs = element.exprNoAssignmentList
        val actualParamCount = actualParamsExprs.count()
        for (reference in funcReference.resolvedReferences) {
            val functionHeader = reference as? GlslNamedFunctionHeader ?: continue
            val parameterDeclarators = functionHeader.getParameterDeclarators()
            if (parameterDeclarators.count() == actualParamCount) {
                return
            }
        }
        val textRange = TextRange(actualParamsExprs.first().startOffset, actualParamsExprs.last().endOffset)
        setHighlightingError(textRange, holder, "Incorrect number of parameters")
    }

    /**
     *
     */
    private fun annotateFunctionCall(element: GlslFunctionCall, holder: AnnotationHolder) {
        annotateIncorrectParamCount(element, holder)
    }

    /**
     *
     */
    private fun setHighlightingError(element: PsiElement?, holder: AnnotationHolder, message: String) {
        if (element == null) return
        holder.newAnnotation(HighlightSeverity.ERROR, message)
            .highlightType(ProblemHighlightType.GENERIC_ERROR)
            .range(element)
            .create()
    }

    /**
     *
     */
    private fun setHighlightingError(range: TextRange, holder: AnnotationHolder, message: String) {
        holder.newAnnotation(HighlightSeverity.ERROR, message)
            .highlightType(ProblemHighlightType.GENERIC_ERROR)
            .range(range)
            .create()
    }
}