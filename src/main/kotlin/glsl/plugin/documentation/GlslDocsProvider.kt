package glsl.plugin.documentation

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.intellij.psi.util.prevLeaf
import glsl.GlslTypes
import glsl.plugin.psi.named.GlslNamedElement
import glsl.plugin.utils.GlslBuiltinUtils
import glsl.plugin.utils.GlslUtils
import glsl.psi.interfaces.GlslExternalDeclaration
import glsl.psi.interfaces.GlslFunctionHeader
import glsl.psi.interfaces.GlslFunctionPrototype
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 *
 */
class GlslDocsProvider : AbstractDocumentationProvider() {
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
        editor: Editor, file: PsiFile,
        contextElement: PsiElement?, targetOffset: Int
    ): PsiElement? {
        if (contextElement?.text in GlslBuiltinUtils.getBuiltinFuncs().keys) {
            return contextElement
        }
        return super.getCustomDocumentationElement(editor, file, contextElement, targetOffset)
    }

    /**
    *
    */
    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String {
        val elementText = element?.text
        if (elementText != null && elementText in GlslBuiltinUtils.getBuiltinFuncs().keys) {
            return document?.getElementById(elementText).toString()
        } else if (element is GlslFunctionHeader) {
            return getFuncDocString(element)
        }
        return convertTextToHtml(element?.text)
    }

    /**
     *
     */
    private fun getFuncDocString(element: GlslNamedElement): String {
        val externalDeclaration = PsiTreeUtil.getParentOfType(element, GlslExternalDeclaration::class.java)
        var commentElement = externalDeclaration?.prevLeaf { it.elementType != TokenType.WHITE_SPACE }
        var commentText = commentElement?.text
        while (commentElement != null && commentElement.elementType == GlslTypes.MULTILINE_COMMENT) {
            commentElement = commentElement.prevLeaf { it.elementType != TokenType.WHITE_SPACE }
            commentText += commentElement?.text?.replace("^ *(\\*/|/\\*\\*|\\*)".toRegex(RegexOption.MULTILINE), "")
        }
        if (commentText == ";" || commentText.isNullOrEmpty()) {
            val funcText = PsiTreeUtil.getParentOfType(element, GlslFunctionPrototype::class.java)?.text
            return convertTextToHtml(funcText)
        } else {
            return convertTextToHtml(commentText)
        }
    }

    /**
     *
     */
    private fun convertTextToHtml(text: String?): String {
        if (text == null) return ""
        return text.replace("\n", "<br>")
    }

}
