package glsl.plugin.psi.named.variables

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.icons.AllIcons
import com.intellij.lang.ASTNode
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.util.parentOfType
import glsl.plugin.code.highlighting.GlslTextAttributes
import glsl.plugin.psi.GlslType
import glsl.plugin.psi.named.GlslNamedElement
import glsl.plugin.psi.named.GlslNamedType
import glsl.plugin.psi.named.GlslNamedVariableImpl
import glsl.plugin.utils.GlslUtils
import glsl.psi.interfaces.GlslFunctionHeader
import glsl.psi.interfaces.GlslFunctionPrototype
import glsl.psi.interfaces.GlslVariableIdentifier
import javax.swing.Icon

/**
 *
 */
abstract class GlslNamedFunctionHeader(node: ASTNode) : GlslNamedVariableImpl(node) {

    /**
     *
     */
    override fun getPsi(): GlslFunctionHeader {
        return this as GlslFunctionHeader
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
        val functionPrototype = getPsi().parentOfType<GlslFunctionPrototype>() ?: return null
        return GlslUtils.getFunctionLookupElement(functionPrototype, getLookupIcon())
    }

    /**
     *
     */
    fun getParameterDeclarators(): List<GlslNamedElement> {
        val funcPrototype = getPsi().parent as GlslFunctionPrototype? ?: return emptyList()
        return funcPrototype.funcHeaderWithParams?.parameterDeclaratorList ?: emptyList()
    }
}