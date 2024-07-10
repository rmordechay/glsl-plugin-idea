package glsl.plugin.features

import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.html.HtmlTag
import com.intellij.psi.util.childrenOfType
import com.intellij.psi.xml.XmlTag
import com.intellij.psi.xml.XmlText
import glsl.plugin.language.GlslLanguage

class GlslMultiHostInjector : MultiHostInjector {
    /**
     *
     */
    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
        val htmlTag = context as? HtmlTag
        val typeText = htmlTag?.getAttribute("type")?.value
        if (typeText?.contains("x-shader") != true) return

        val xmlText = htmlTag.childrenOfType<XmlText>().firstOrNull() ?: return
        val textRangeInParent = xmlText.firstChild.textRangeInParent
        registrar.startInjecting(GlslLanguage.INSTANCE)
            .addPlace(null, null, xmlText as PsiLanguageInjectionHost, textRangeInParent)
            .doneInjecting()
    }

    /**
     *
     */
    override fun elementsToInjectIn(): MutableList<out Class<out PsiElement>> {
        return mutableListOf(XmlTag::class.java)
    }
}