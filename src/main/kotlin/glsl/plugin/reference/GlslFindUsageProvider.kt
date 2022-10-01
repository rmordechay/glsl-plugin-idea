package glsl.plugin.reference

import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import glsl.GlslTypes
import glsl.plugin.language.GlslLexerAdapter
import glsl.plugin.psi.named.GlslNamedElement
import glsl.plugin.utils.GlslBuiltinUtils
import glsl.psi.interfaces.GlslStructSpecifier

/**
 *
 */
class GlslFindUsageProvider : FindUsagesProvider {

    /**
    *
    */
    override fun getWordsScanner(): WordsScanner {
        return DefaultWordsScanner(
            GlslLexerAdapter(),
            TokenSet.create(GlslTypes.IDENTIFIER),
            TokenSet.create(GlslTypes.LINE_COMMENT, GlslTypes.MULTILINE_COMMENT),
            TokenSet.EMPTY
        )
    }

    /**
    *
    */
    override fun canFindUsagesFor(psiElement: PsiElement): Boolean {
        if (psiElement !is GlslNamedElement) return false
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