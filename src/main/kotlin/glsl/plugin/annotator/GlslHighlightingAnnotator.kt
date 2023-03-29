package glsl.plugin.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement
import glsl.plugin.code.highlighting.GlslTextAttributes
import glsl.plugin.psi.GlslIdentifierImpl
import glsl.plugin.psi.named.GlslNamedElement
import glsl.plugin.utils.GlslBuiltinUtils.isBuiltinConstant
import glsl.plugin.utils.GlslBuiltinUtils.isBuiltinName
import glsl.psi.interfaces.GlslLayoutQualifierId


/**
 *
 */
class GlslHighlightingAnnotator : Annotator {

    /**
     *
     */
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is GlslIdentifierImpl) return
        val extension = holder.currentAnnotationSession.file.virtualFile.extension
        val elementName = element.name
        if (isBuiltinName(elementName, extension)) {
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
    private fun setDeclarationHighlighting(element: GlslIdentifierImpl, holder: AnnotationHolder) {
        if (element.parent is GlslLayoutQualifierId) {
            createAnnotation(holder, GlslTextAttributes.VARIABLE_TEXT_ATTR)
        } else if (element.getAsNamedElement() != null) {
            createAnnotation(holder, element.getAsNamedElement()!!.getHighlightTextAttr())
        }
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
