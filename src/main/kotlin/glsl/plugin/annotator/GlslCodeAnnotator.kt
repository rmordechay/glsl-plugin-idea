package glsl.plugin.annotator

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import glsl.data.GlslDefinitions.SCALARS_CONSTRUCTORS
import glsl.data.GlslErrorMessages
import glsl.data.GlslErrorMessages.Companion.CONDITION_MUST_BE_BOOL
import glsl.data.GlslErrorMessages.Companion.CONSTRUCTOR_PRIMITIVE_ONE_ARGUMENT
import glsl.data.GlslErrorMessages.Companion.INCOMPATIBLE_TYPES_IN_ASSIGNMENT
import glsl.data.GlslErrorMessages.Companion.INCOMPATIBLE_TYPES_IN_INIT
import glsl.data.GlslErrorMessages.Companion.INVALID_TYPES_ARGUMENT_CONSTRUCTOR
import glsl.data.GlslErrorMessages.Companion.TOO_FEW_ARGUMENTS_CONSTRUCTOR
import glsl.data.GlslErrorMessages.Companion.TOO_MANY_ARGUMENTS_CONSTRUCTOR
import glsl.data.GlslErrorMessages.Companion.TYPES_CONDITIONAL_EXPR_NO_MATCH
import glsl.plugin.psi.GlslType
import glsl.plugin.psi.builtins.GlslScalar
import glsl.plugin.psi.builtins.GlslVector
import glsl.plugin.psi.named.GlslNamedElement
import glsl.plugin.psi.named.GlslNamedFunctionHeader
import glsl.plugin.psi.named.GlslNamedUserType
import glsl.psi.interfaces.*


class GlslCodeAnnotator : Annotator {

    /**
     *
     */
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        when (element) {
            is GlslSingleDeclaration -> {
                annotateSingleDeclaration(element, holder)
            }
            is GlslAssignmentExpr -> {
                annotateAssignmentExpr(element, holder)
            }
            is GlslConditionalExpr -> {
                annotateConditionalExpr(element, holder)
            }
            is GlslFunctionCall -> {
                val exprList = element.exprNoAssignmentList
                if (element.variableIdentifier != null) {
                    annotateFunctionCall(element.variableIdentifier!!, exprList, holder)
                }
                else if (element.typeSpecifier != null) {
                    annotateConstructor(element.typeSpecifier!!, exprList, holder)
                }
            }
        }
    }

    /**
     *
     */
    private fun annotateFunctionCall(variableIdentifier: GlslVariableIdentifier, exprList: List<GlslExpr>, holder: AnnotationHolder) {
        val resolvedFunction = variableIdentifier.reference?.resolve()
        val funcName = variableIdentifier.text
        var paramDeclarators: List<GlslNamedElement>? = null
        if (resolvedFunction is GlslNamedFunctionHeader) {
            paramDeclarators = resolvedFunction.getParameterDeclarators()
        } else if (resolvedFunction is GlslNamedUserType) {
            paramDeclarators = resolvedFunction.getStructMembers()
        }
        if (exprList.isEmpty()) {
            if (paramDeclarators != null && paramDeclarators.isEmpty()) return
            annotateNoMatchingFunc(variableIdentifier, funcName, emptyList(), holder)
        } else {
            val exprTypes = exprList.map { it.getExprType() }
            if (exprTypes.contains(null)) return
            if (paramDeclarators == null || paramDeclarators.size != exprTypes.size) {
                val exprTypesText = exprTypes.mapNotNull { it!!.getTypeText() }
                annotateNoMatchingFunc(variableIdentifier, funcName, exprTypesText, holder)
                return
            }
            for ((i, paramDeclarator) in paramDeclarators.withIndex()) {
                val declaratorType = paramDeclarator.getAssociatedType()
                if (exprTypes.size < i) continue
                val typesEqual = exprTypes[i]!!.isEqual(declaratorType)
                if (typesEqual == false) {
                    val exprTypesText = exprTypes.mapNotNull { it!!.getTypeText() }
                    annotateNoMatchingFunc(variableIdentifier, funcName, exprTypesText, holder)
                }
            }
        }

    }

    /**
     *
     */
    private fun annotateSingleDeclaration(singleDeclaration: GlslSingleDeclaration, holder: AnnotationHolder) {
        for (expr in singleDeclaration.exprNoAssignmentList) {
            val foundType = expr.getExprType()
            val requiredType = singleDeclaration.getAssociatedType()
            val requiredText = requiredType?.getTypeText()
            if (requiredText?.isBlank() == true) continue
            if (foundType?.isEqual(requiredType) == false) {
                val msg = " Required: ${requiredType?.getTypeText() ?: ""}; Found: ${foundType.getTypeText()}."
                setHighlightingError(singleDeclaration.variableIdentifier, holder, INCOMPATIBLE_TYPES_IN_INIT + msg)
            }
        }
    }

    /**
     *
     */
    private fun annotateAssignmentExpr(element: GlslAssignmentExpr, holder: AnnotationHolder) {
        val assignmentType = element.assignmentOperator.text
        if (setOf('*', '/', '+', '-').contains(assignmentType.first())) return

        val exprList = element.exprList
        val leftType = exprList.first().getExprType()
        val rightType = exprList.last().getExprType()
        if (rightType == null || leftType == null) return
        if (rightType.isEqual(leftType) == true) {
            setHighlightingError(exprList.first(), holder,  INCOMPATIBLE_TYPES_IN_ASSIGNMENT.format(leftType.getTypeText(), rightType.getTypeText()))
        }
    }

    /**
     *
     */
    private fun annotateConstructor(typeSpecifier: GlslTypeSpecifier, exprList: List<GlslExpr>, holder: AnnotationHolder) {
        val constructorType = GlslType.getInstance(typeSpecifier) ?: return
        if (constructorType is GlslVector) {
            annotateVecConstructor(typeSpecifier, exprList, constructorType, holder)
        } else if (constructorType is GlslScalar) {
            annotateScalarConstructor(typeSpecifier, exprList, constructorType, holder)
        }
    }

    /**
     *
     */
    private fun annotateVecConstructor(typeSpecifier: GlslTypeSpecifier, exprList: List<GlslExpr>, vector: GlslVector, holder: AnnotationHolder) {
        if (exprList.size == 1) return
        val constructorDim = vector.getDimension()
        val exprDimSum = exprList.sumOf { it.getExprType()?.getDimension() ?: 0 }
        if (constructorDim > exprDimSum) {
            val msg = TOO_FEW_ARGUMENTS_CONSTRUCTOR.format(vector.getTypeText())
            setHighlightingError(typeSpecifier, holder, msg)
        } else if (constructorDim < exprDimSum) {
            val msg = TOO_MANY_ARGUMENTS_CONSTRUCTOR.format(vector.getTypeText())
            setHighlightingError(typeSpecifier, holder, msg)
        }
    }

    /**
     *
     */
    private fun annotateScalarConstructor(typeSpecifier: GlslTypeSpecifier, exprList: List<GlslExpr>, scalar: GlslScalar, holder: AnnotationHolder) {
        if (exprList.isEmpty()) {
            setHighlightingError(typeSpecifier, holder, CONSTRUCTOR_PRIMITIVE_ONE_ARGUMENT)
        } else if (exprList.size > 1) {
            val msg = TOO_MANY_ARGUMENTS_CONSTRUCTOR.format(scalar.getTypeText())
            setHighlightingError(typeSpecifier, holder, msg)
        } else {
            val exprTypeText = exprList.first().getExprType()?.getTypeText()
            if (!SCALARS_CONSTRUCTORS.contains(exprTypeText)) {
                val firstArgument = 1
                val msg = INVALID_TYPES_ARGUMENT_CONSTRUCTOR.format(exprTypeText, firstArgument, scalar.getTypeText())
                setHighlightingError(typeSpecifier, holder, msg)
            }
        }
    }

    /**
     *
     */
    private fun annotateConditionalExpr(element: GlslConditionalExpr, holder: AnnotationHolder) {
        val firstExpr = element.exprNoAssignmentList.first()
        if (firstExpr?.getExprType()?.getTypeText() != "bool") {
            setHighlightingError(firstExpr, holder, CONDITION_MUST_BE_BOOL)
        }
        val secondExpr = element.exprNoAssignmentList[1]
        val thirdExpr = element.exprNoAssignmentList[2]
        val secondExprType = secondExpr.getExprType() ?: return
        val thirdExprType = thirdExpr.getExprType()
        if (secondExprType.isEqual(thirdExprType) == true) {
            val msg = TYPES_CONDITIONAL_EXPR_NO_MATCH.format(secondExprType.getTypeText(), thirdExprType?.getTypeText())
            val range = TextRange(secondExpr.startOffset, thirdExpr.endOffset)
            setHighlightingError(range, holder, msg)
        }
    }

    /**
     *
     */
    private fun annotateNoMatchingFunc(functionCall: PsiElement, funcName: String, exprTypes: List<String>, holder: AnnotationHolder) {
        val msg = GlslErrorMessages.NO_MATCHING_FUNCTION_CALL.format(funcName, exprTypes.joinToString(", "))
        setHighlightingError(functionCall, holder, msg)
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