package glsl.plugin.annotator

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil.collectElements
import com.intellij.psi.util.elementType
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import glsl.GlslTypes.RETURN
import glsl.data.GlslErrorMessages.Companion.INCOMPATIBLE_TYPES_IN_INIT
import glsl.data.GlslErrorMessages.Companion.MISSING_RETURN_FUNCTION
import glsl.data.GlslErrorMessages.Companion.NO_MATCHING_FUNCTION_CALL
import glsl.data.GlslErrorMessages.Companion.TOO_FEW_ARGUMENTS_CONSTRUCTOR
import glsl.data.GlslErrorMessages.Companion.TOO_MANY_ARGUMENTS_CONSTRUCTOR
import glsl.psi.impl.GlslFunctionDeclaratorImpl
import glsl.psi.interfaces.*


class GlslCodeAnnotator : Annotator {

    /**
     *
     */
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        when (element) {
            is GlslFunctionDefinition -> {
                annotateMissingReturn(element, holder)
            }
            is GlslSingleDeclaration -> {
//                annotateSingleDeclaration(element, holder)
            }
            is GlslFunctionCall -> {
//                annotateNoMatchingFunction(element, holder)
            }
        }
    }

    /**
     *
     */
    private fun annotateMissingReturn(element: GlslFunctionDefinition, holder: AnnotationHolder) {
        val functionDeclarator = element.functionDeclarator
        if (functionDeclarator.typeSpecifier.textMatches("void")) return
        val returnExists = collectElements(element) { e -> e.elementType == RETURN }.isNotEmpty()
        if (returnExists) return
        val textRange = TextRange(element.endOffset - 1, element.endOffset)
        val funcName = functionDeclarator.name
        val msg = MISSING_RETURN_FUNCTION.format(funcName)
        setHighlightingError(textRange, holder, msg)
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