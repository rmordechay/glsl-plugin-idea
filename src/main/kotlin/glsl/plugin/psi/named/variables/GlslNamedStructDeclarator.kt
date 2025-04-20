package glsl.plugin.psi.named.variables

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.icons.AllIcons
import com.intellij.lang.ASTNode
import com.intellij.openapi.editor.colors.TextAttributesKey
import glsl.plugin.editor.highlighting.GlslTextAttributes
import glsl.plugin.psi.named.GlslNamedType
import glsl.plugin.psi.named.GlslNamedVariableImpl
import glsl.plugin.utils.GlslUtils
import glsl.psi.interfaces.GlslStructDeclaration
import glsl.psi.interfaces.GlslStructDeclarator
import glsl.psi.interfaces.GlslVariableIdentifier
import javax.swing.Icon

/**
 *
 */
abstract class GlslNamedStructDeclarator(node: ASTNode) : GlslNamedVariableImpl(node) {

    /**
     *
     */
    override fun getPsi(): GlslStructDeclarator {
        return this as GlslStructDeclarator
    }

    /**
     *
     */
    override fun getLookupIcon(): Icon? {
        return AllIcons.Nodes.Field
    }

    /**
     *
     */
    override fun getAssociatedType(): GlslNamedType? {
        val structDeclaration = getPsi().parent as GlslStructDeclaration
        val typeSpecifier = structDeclaration.typeSpecifier ?: return null
        return GlslUtils.getType(typeSpecifier)
    }

    /**
     *
     */
    override fun getNameIdentifier(): GlslVariableIdentifier? {
        return getPsi().variableIdentifier
    }

    /**
     *
     */
    override fun getHighlightTextAttr(): TextAttributesKey {
        return GlslTextAttributes.VARIABLE_TEXT_ATTR
    }

    /**
     *
     */
    override fun getLookupElement(returnTypeText: String?): LookupElement? {
        val name = getPsi().name ?: return null
        val typeText = getAssociatedType()?.text
        return GlslUtils.createLookupElement(name, icon = getLookupIcon(), returnTypeText = typeText)
    }
}