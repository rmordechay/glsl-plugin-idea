package glsl.plugin.features

import com.intellij.lang.documentation.DocumentationProvider
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import glsl.plugin.utils.GlslBuiltinUtils.isBuiltinFunction
import glsl.plugin.utils.GlslUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 *
 */
class GlslDocumentationProvider : DocumentationProvider {
    private var document: Document? = null

    init {
        val fileText = GlslUtils.getResourceFileAsString("builtin-objects/builtin-funcs-docs.html")
        if (fileText != null) {
            document = Jsoup.parse(fileText)
        }
    }

    /**
     *
     */
    override fun getCustomDocumentationElement(
        editor: Editor,
        file: PsiFile,
        contextElement: PsiElement?,
        targetOffset: Int
    ): PsiElement? {
        if (isBuiltinFunction(contextElement?.text)) {
            return contextElement
        }
        return null
    }

    /**
     *
     */
    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
        val elementText = element?.text
        if (isBuiltinFunction(elementText)) {
            return document?.getElementById(elementText!!).toString()
        }
        return null
    }
}
