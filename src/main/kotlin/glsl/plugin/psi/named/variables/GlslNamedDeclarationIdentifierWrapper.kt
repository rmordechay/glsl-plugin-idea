package glsl.plugin.psi.named.variables

import com.intellij.lang.ASTNode
import com.intellij.openapi.editor.colors.TextAttributesKey
import glsl.plugin.code.highlighting.GlslTextAttributes
import glsl.plugin.psi.GlslType
import glsl.plugin.psi.named.GlslNamedVariableImpl
import glsl.psi.interfaces.GlslDeclarationIdentifierWrapper
import glsl.psi.interfaces.GlslVariableIdentifier
import javax.swing.Icon

/**
 *
 */
abstract class GlslNamedDeclarationIdentifierWrapper(node: ASTNode) : GlslNamedVariableImpl(node) {

    /**
     *
     */
    override fun getPsi(): GlslDeclarationIdentifierWrapper {
        return this as GlslDeclarationIdentifierWrapper
    }

    /**
     *
     */
    override fun getLookupIcon(): Icon? {
        return null
    }

    /**
     *
     */
    override fun getAssociatedType(): GlslType? {
        return null
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


}