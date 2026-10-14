package glsl.plugin.psi.named

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import glsl.plugin.psi.GlslIdentifier
import glsl.plugin.utils.GlslUtils
import javax.swing.Icon

interface GlslNamedElement : PsiNameIdentifierOwner {
    /**
     *
     */
    override fun setName(newName: String): PsiElement {
        (nameIdentifier as? GlslIdentifier)?.replaceElementName(newName)
        return this
    }

    /**
     * A bit of a weird function which solely casts the same object to the appropriate named element
     * in order to avoid method-injection, which can be confusing.
     */
    fun getPsi(): GlslNamedElement

    /**
     * Syntax highlighting color
     */
    fun getHighlightTextAttr(): TextAttributesKey

    /**
     * Used for getVariants()
     */
    fun getLookupElement(returnTypeText: String? = null): LookupElement?

    /**
     * The icon that is shown in the popup completion.
     */
    fun getLookupIcon(): Icon?

    /**
     * Get the type of identifier in case it's a variable or itself in case the identifier is a type
     */
    fun getAssociatedType(): GlslNamedType?
}

abstract class GlslNamedElementImpl(node: ASTNode) : ASTWrapperPsiElement(node), GlslNamedElement {

    /**
    *
    */
    override fun getName(): String? {
        return nameIdentifier?.text ?: return ""
    }

    /**
    *
    */
    override fun getTextOffset(): Int {
        return nameIdentifier?.textOffset ?: super.getTextOffset()
    }

    /**
    *
    */
    override fun getLookupElement(returnTypeText: String?): LookupElement? {
        val lookupString = getPsi().name ?: return null
        return GlslUtils.createLookupElement(lookupString, icon = getLookupIcon(), withBoldness = true)
    }
}