package glsl.plugin.psi.builtins

import com.intellij.icons.AllIcons
import com.intellij.lang.ASTNode
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement
import glsl.plugin.code.highlighting.GlslTextAttributes
import glsl.plugin.psi.named.GlslNamedTypeImpl
import glsl.plugin.psi.named.GlslNamedVariable
import javax.swing.Icon

/**
 *
 */
abstract class GlslBuiltinType(node: ASTNode) : GlslNamedTypeImpl(node) {

    /**
     *
     */
    override fun getPsi(): GlslBuiltinType {
        return this
    }

    /**
     *
     */
    override fun getHighlightTextAttr(): TextAttributesKey {
        return GlslTextAttributes.BUILTIN_TYPE_TEXT_ATTR
    }

    /**
     *
     */
    override fun getLookupIcon(): Icon? {
        return AllIcons.Nodes.Type
    }

    /**
     *
     */
    override fun getNameIdentifier(): PsiElement? {
        return this
    }

    /**
     *
     */
    override fun getStructMembers(): List<GlslNamedVariable> {
        return emptyList()
    }

    /**
     *
     */
    override fun getStructMember(memberName: String): GlslNamedVariable? {
        return null
    }

    /**
     *
     */
    override fun isConvertible(other: String): Boolean {
        return false
    }

    /**
     *
     */
    override fun getDimension(): Int {
        return 1
    }

}