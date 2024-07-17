package glsl.plugin.psi.named.types.builtins

import com.intellij.icons.AllIcons
import com.intellij.lang.ASTNode
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement
import com.intellij.psi.util.firstLeaf
import glsl.data.GlslDefinitions
import glsl.plugin.code.highlighting.GlslTextAttributes
import glsl.plugin.psi.named.GlslNamedTypeImpl
import glsl.plugin.psi.named.GlslNamedVariable
import glsl.psi.interfaces.GlslBuiltinTypeScalar
import javax.swing.Icon

/**
 *
 */
abstract class GlslScalar(node: ASTNode) : GlslNamedTypeImpl(node), GlslBuiltinType {
    /**
     *
     */
    override fun getPsi(): GlslBuiltinTypeScalar {
        return this as GlslBuiltinTypeScalar
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
        return firstLeaf()
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
        val implicitConversions = GlslDefinitions.SCALARS[name]
        return implicitConversions?.contains(other) ?: false
    }

    /**
     *
     */
    override fun getDimension(): Int {
        return 1
    }
}
