package glsl.plugin.reference

import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import glsl.GlslTypes
import glsl.GlslTypes.STRING_LITERAL
import glsl.data.GlslTokenSets.COMMENTS
import glsl.plugin.language.GlslLexerAdapter
import glsl.plugin.psi.named.GlslNamedElement
import glsl.plugin.utils.GlslBuiltinUtils
import glsl.psi.interfaces.GlslStructSpecifier

/**
 *
 */
class GlslFindUsageProvider : FindUsagesProvider {
    private var currentFileName: String? = null
    private var project: Project? = null

    /**
    *
    */
    override fun getWordsScanner(): WordsScanner {
        return DefaultWordsScanner(
            GlslLexerAdapter(project, currentFileName),
            TokenSet.create(GlslTypes.IDENTIFIER),
            COMMENTS,
            TokenSet.create(STRING_LITERAL)
        )
    }

    /**
    *
    */
    override fun canFindUsagesFor(psiElement: PsiElement): Boolean {
        if (psiElement !is GlslNamedElement) return false
        currentFileName = psiElement.containingFile.name
        project = psiElement.project
        val structSpecifier = psiElement.parent?.parent as? GlslStructSpecifier ?: return true
        return GlslBuiltinUtils.isBuiltin(structSpecifier.name)
    }

    /**
    *
    */
    override fun getHelpId(psiElement: PsiElement): String? {
        return null
    }

    /**
    *
    */
    override fun getType(element: PsiElement): String {
        if (element !is GlslNamedElement) return ""
        return "declaration"
    }

    /**
    *
    */
    override fun getDescriptiveName(element: PsiElement): String {
        return if (element !is GlslNamedElement || element.name == null) "" else element.name!!
    }

    /**
    *
    */
    override fun getNodeText(element: PsiElement, useFullName: Boolean): String {
        return if (element !is GlslNamedElement) "" else element.text
    }
}