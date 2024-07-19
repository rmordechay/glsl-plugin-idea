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
//            is JSStringTemplateExpression -> {
//                val stringTemplate = context.childLeafs().find { it.elementType == STRING_TEMPLATE_PART } ?: return
//                if (!stringTemplate.text.startsWith(JS_STRING_GLSL_PREFIX)) return
//                val startStringOffset = stringTemplate.textRangeInParent.startOffset + JS_STRING_GLSL_PREFIX.length
//                val endStringOffset = stringTemplate.textRangeInParent.endOffset
//                val textRange = TextRange(startStringOffset, endStringOffset)
//                registrar.startInjecting(GlslLanguage.INSTANCE)
//                    .addPlace(null, null, context as PsiLanguageInjectionHost, textRange)
//                    .doneInjecting()
//            }
//            is JSLiteralExpression -> {
//                registrar.startInjecting(GlslLanguage.INSTANCE)
//                    .addPlace(null, null, context as PsiLanguageInjectionHost, (context as JSLiteralExpression).textRangeInParent)
//                    .doneInjecting()
//            }
        }
    }

    /**
     *
     */
    override fun elementsToInjectIn(): MutableList<out Class<out PsiElement>> {
//        return mutableListOf(XmlTag::class.java, JSLiteralExpression::class.java, JSStringTemplateExpression::class.java)
        return mutableListOf(XmlTag::class.java)
    }
}