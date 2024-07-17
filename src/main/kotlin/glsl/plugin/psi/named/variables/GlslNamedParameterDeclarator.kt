package glsl.plugin.psi.named.variables

import com.intellij.icons.AllIcons
import com.intellij.lang.ASTNode
import com.intellij.openapi.editor.colors.TextAttributesKey
import glsl.plugin.code.highlighting.GlslTextAttributes
import glsl.plugin.psi.named.GlslNamedType
import glsl.plugin.psi.named.GlslNamedVariableImpl
import glsl.plugin.utils.GlslUtils
import glsl.psi.interfaces.GlslParameterDeclarator
import glsl.psi.interfaces.GlslVariableIdentifier
import javax.swing.Icon

/**
 *
 */
abstract class GlslNamedParameterDeclarator(node: ASTNode) : GlslNamedVariableImpl(node) {
    /**
     *
     */
    override fun getPsi(): GlslParameterDeclarator {
        return this as GlslParameterDeclarator
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
    override fun getAssociatedType(): GlslNamedType? {
        return GlslUtils.getType(getPsi().typeSpecifier)
    }

    /**
     *
     */
    override fun getLookupIcon(): Icon {
        return AllIcons.Nodes.Parameter
    }

    /**
     *
     */
    override fun getHighlightTextAttr(): TextAttributesKey {
        return GlslTextAttributes.FUNC_PARAM_TEXT_ATTR
    }
}