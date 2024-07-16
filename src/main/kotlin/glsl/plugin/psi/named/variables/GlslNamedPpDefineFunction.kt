package glsl.plugin.psi.named.variables

import com.intellij.icons.AllIcons
import com.intellij.lang.ASTNode
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.util.childrenOfType
import glsl.plugin.code.highlighting.GlslTextAttributes
import glsl.plugin.psi.GlslType
import glsl.plugin.psi.named.GlslNamedVariableImpl
import glsl.psi.interfaces.GlslPpDefineFunction
import glsl.psi.interfaces.GlslVariableIdentifier
import javax.swing.Icon

/**
 *
 */
abstract class GlslNamedPpDefineFunction(node: ASTNode) : GlslNamedVariableImpl(node) {
    /**
     *
     */
    override fun getPsi(): GlslPpDefineFunction {
        return this as GlslPpDefineFunction
    }

    /**
     *
     */
    override fun getNameIdentifier(): GlslVariableIdentifier? {
        return getPsi().childrenOfType<GlslVariableIdentifier>().firstOrNull()
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
    override fun getLookupIcon(): Icon {
        return AllIcons.Nodes.Function
    }

    /**
     *
     */
    override fun getHighlightTextAttr(): TextAttributesKey {
        return GlslTextAttributes.MACRO_FUNC_NAME_ATTR
    }
}