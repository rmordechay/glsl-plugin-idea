package glsl.plugin.psi.named.variables

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.icons.AllIcons
import com.intellij.lang.ASTNode
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.util.parentOfType
import glsl.plugin.editor.highlighting.GlslTextAttributes
import glsl.plugin.psi.named.GlslNamedType
import glsl.plugin.psi.named.GlslNamedVariableImpl
import glsl.plugin.utils.GlslUtils
import glsl.psi.interfaces.GlslFunctionDeclarator
import glsl.psi.interfaces.GlslVariableIdentifier
import javax.swing.Icon

/**
 *
 */
abstract class GlslNamedFunctionDeclarator(node: ASTNode) : GlslNamedVariableImpl(node) {

    /**
     *
     */
    override fun getPsi(): GlslFunctionDeclarator {
        return this as GlslFunctionDeclarator
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
    override fun getHighlightTextAttr(): TextAttributesKey {
        return GlslTextAttributes.FUNC_TEXT_ATTR
    }

    /**
     *
     */
    override fun getLookupElement(returnTypeText: String?): LookupElement? {
        val functionPrototype = getPsi().parentOfType<GlslFunctionDeclarator>(true) ?: return null
        return GlslUtils.getFunctionLookupElement(functionPrototype, getLookupIcon())
    }

    /**
     *
     */
    fun getParameterTypes(): List<GlslNamedType>? {
        return getPsi().funcHeaderWithParams
            ?.parameterDeclaratorList
            ?.mapNotNull { it.getAssociatedType() }
    }
}