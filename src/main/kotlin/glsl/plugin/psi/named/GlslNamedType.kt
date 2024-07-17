package glsl.plugin.psi.named

import com.intellij.icons.AllIcons
import com.intellij.lang.ASTNode
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.elementType
import com.intellij.psi.util.firstLeaf
import glsl.GlslTypes
import glsl.plugin.code.highlighting.GlslTextAttributes
import glsl.plugin.psi.named.types.builtins.GlslScalar
import javax.swing.Icon


/**
 *
 */
interface GlslNamedType : GlslNamedElement {
    fun getStructMembers(): List<GlslNamedVariable>
    fun getStructMember(memberName: String): GlslNamedVariable?
    fun getDimension(): Int
    fun typeAsToken(): IElementType?
    fun canCast(other: IElementType?): Boolean

    /**
     *
     */
    fun isEqual(other: GlslNamedType?): Boolean {
        val thisElementType = typeAsToken() ?: return false
        val otherElementType = other?.typeAsToken() ?: return false
        return thisElementType == otherElementType || canCast(otherElementType)
    }
}

/**
 *
 */
abstract class GlslNamedTypeImpl(node: ASTNode) : GlslNamedElementImpl(node), GlslNamedType {
    /**
     *
     */
    override fun typeAsToken(): IElementType? {
        return firstLeaf().elementType
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
}

