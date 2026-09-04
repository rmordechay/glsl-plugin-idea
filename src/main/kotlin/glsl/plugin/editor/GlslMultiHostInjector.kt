package glsl.plugin.editor

import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.html.HtmlTag
import com.intellij.psi.util.childrenOfType
import com.intellij.psi.util.elementType
import com.intellij.psi.xml.XmlTag
import com.intellij.psi.xml.XmlText
import glsl.GlslTypes
import glsl.plugin.language.GlslFile
import glsl.plugin.language.GlslLanguage

class GlslTemplateContext : TemplateContextType( "GLSL") {
    override fun isInContext(templateActionContext: TemplateActionContext): Boolean {
        val file = templateActionContext.file
        if (file is GlslFile) {
            val elemType = file.findElementAt(templateActionContext.startOffset)?.elementType
            return when(elemType) {
                GlslTypes.STRING_LITERAL, GlslTypes.LINE_COMMENT, GlslTypes.MULTILINE_COMMENT -> false
                else -> true
            }
        }
        return false
    }
}

class GlslMultiHostInjector : MultiHostInjector {
    /**
     *
     */
    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
        when (context) {
            is HtmlTag -> {
                val typeText = context.getAttribute("type")?.value ?: return
                if (!typeText.contains("x-shader")) return
                val xmlText = context.childrenOfType<XmlText>().firstOrNull() ?: return
                val textRangeInParent = xmlText.firstChild.textRangeInParent
                registrar.startInjecting(GlslLanguage.INSTANCE)
                    .addPlace(null, null, xmlText as PsiLanguageInjectionHost, textRangeInParent)
                    .doneInjecting()
            }
        }
    }

    /**
     *
     */
    override fun elementsToInjectIn(): MutableList<out Class<out PsiElement>> {
        return mutableListOf(XmlTag::class.java)
    }
}