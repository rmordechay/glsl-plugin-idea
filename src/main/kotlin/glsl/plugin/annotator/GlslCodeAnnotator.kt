package glsl.plugin.annotator

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.childLeafs
import com.intellij.psi.util.elementType
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import glsl.GlslTypes.IDENTIFIER
import glsl.GlslTypes.RETURN
import glsl.data.GlslErrorMessages.Companion.INCOMPATIBLE_TYPES_IN_INIT
import glsl.data.GlslErrorMessages.Companion.MISSING_RETURN_FUNCTION
import glsl.data.GlslErrorMessages.Companion.NO_MATCHING_FUNCTION_CALL
import glsl.plugin.psi.named.GlslNamedFunctionHeader
import glsl.psi.interfaces.GlslFunctionCall
import glsl.psi.interfaces.GlslFunctionDefinition
import glsl.psi.interfaces.GlslSingleDeclaration
import glsl.psi.interfaces.GlslStructSpecifier


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
                annotateSingleDeclaration(element, holder)
            }
            is GlslFunctionCall -> {
                annotateNoMatchingFunction(element, holder)
            }
        }
    }

    /**
     *
     */
    private fun annotateSingleDeclaration(singleDeclaration: GlslSingleDeclaration, holder: AnnotationHolder) {
        val expr = singleDeclaration.exprNoAssignmentList.firstOrNull() ?: return
        val declarationType = singleDeclaration.getAssociatedType() ?: return
        val exprType = expr.getExprType() ?: return
        if (declarationType.isEqual(exprType)) return
        setHighlightingError(expr, holder, INCOMPATIBLE_TYPES_IN_INIT)
    }

    /**
     *
     */
    private fun annotateNoMatchingFunction(element: GlslFunctionCall, holder: AnnotationHolder) {
        val variableIdentifier = element.variableIdentifier
        if (variableIdentifier?.firstChild.elementType != IDENTIFIER) return
        val funcReference = variableIdentifier?.reference ?: return
        val resolvedReferences = funcReference.resolveMany() ?: return
        if (resolvedReferences.isEmpty()) return
        val actualParamsExprs = element.exprNoAssignmentList
        val actualParamCount = actualParamsExprs.count()
        for (reference in resolvedReferences) {
            if (reference is GlslNamedFunctionHeader) {
                val parameterDeclarators = reference.getParameterDeclarators()
                if (parameterDeclarators.count() == actualParamCount) {
                    return
                }
            } else if (reference is GlslStructSpecifier) {
                val structMembers = reference.getAssociatedType()?.getStructMembers() ?: return
                if (structMembers.count() == actualParamCount) {
                    return
                }
            }
        }
        val textRange: TextRange
        if (actualParamsExprs.isNotEmpty()) {
            textRange = TextRange(actualParamsExprs.first().startOffset, actualParamsExprs.last().endOffset)
        } else {
            textRange = TextRange(element.leftParen.startOffset, element.rightParen.endOffset)
        }
        val actualTypes = actualParamsExprs.mapNotNull { it.getExprType()?.getTypeText() }.joinToString(", ")
        val msg = NO_MATCHING_FUNCTION_CALL.format(variableIdentifier.getName(), actualTypes)
        setHighlightingError(textRange, holder, msg)
    }

    /**
     *
     */
    private fun annotateMissingReturn(element: GlslFunctionDefinition, holder: AnnotationHolder) {
        if (element.functionPrototype.functionHeader.typeSpecifier.textMatches("void")) return
        val returnExists = element.childLeafs().any { it.elementType == RETURN }
        if (returnExists) return
        val textRange = TextRange(element.endOffset - 1, element.endOffset)
        val funcName = element.functionPrototype.functionHeader.name
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