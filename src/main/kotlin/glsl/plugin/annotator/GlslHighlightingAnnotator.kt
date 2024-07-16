package glsl.plugin.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement
import glsl.plugin.code.highlighting.GlslTextAttributes
import glsl.plugin.psi.GlslIdentifier
import glsl.plugin.psi.named.GlslNamedBuiltinType
import glsl.plugin.psi.named.GlslNamedElement
import glsl.plugin.utils.GlslBuiltinUtils.isBuiltinConstant
import glsl.plugin.utils.GlslBuiltinUtils.isBuiltinFunction
import glsl.plugin.utils.GlslBuiltinUtils.isBuiltinShaderVariable
import glsl.psi.interfaces.GlslLayoutQualifierId


/**
 *
 */
class GlslHighlightingAnnotator : Annotator {

    /**
     *
     */
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is GlslIdentifier || element is GlslNamedBuiltinType) return
        val extension = holder.currentAnnotationSession.file.virtualFile.extension
        val elementName = element.getName()
        if (isBuiltinFunction(elementName) || isBuiltinShaderVariable(elementName, extension)) {
            createAnnotation(holder, GlslTextAttributes.BUILTIN_NAME_TEXT_ATTR)
        } else if (isBuiltinConstant(elementName)) {
            createAnnotation(holder, GlslTextAttributes.BUILTIN_GLOBAL_CONSTANTS)
        } else {
            val reference = element.reference?.resolve()
            if (reference != null) {
                setReferenceHighlighting(reference, holder)
            } else {
                setDeclarationHighlighting(element, holder)
            }
        }

    }

    /**
     *
     */
    private fun setReferenceHighlighting(element: GlslNamedElement, holder: AnnotationHolder) {
        val textAttr = element.getHighlightTextAttr()
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .textAttributes(textAttr)
            .create()
    }

    /**
     *
     */
    private fun setDeclarationHighlighting(element: GlslIdentifier, holder: AnnotationHolder) {
        if (element.parent is GlslLayoutQualifierId) {
            createAnnotation(holder, GlslTextAttributes.VARIABLE_TEXT_ATTR)
            return
        }
        val declaration = element.getDeclaration() ?: return
        createAnnotation(holder, declaration.getHighlightTextAttr())
    }

    /**
     *
     */
    private fun createAnnotation(holder: AnnotationHolder, textAttr: TextAttributesKey?) {
        if (textAttr == null) return
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .textAttributes(textAttr)
            .create()
    }
}
