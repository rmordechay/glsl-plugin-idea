package glsl.plugin.psi.named

import com.intellij.icons.AllIcons
import com.intellij.lang.ASTNode
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement
import com.intellij.psi.util.firstLeaf
import glsl.plugin.code.highlighting.GlslTextAttributes
import javax.swing.Icon


/**
 *
 */
interface GlslNamedType : GlslNamedElement {
    fun getStructMembers(): List<GlslNamedVariable>
    fun getStructMember(memberName: String): GlslNamedVariable?
    fun isConvertible(other: String?): Boolean
    fun getDimension(): Int
    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int

//    /**
//     *
//     */
//    fun isEqual(other: GlslNamedType?): Boolean {
//        if (other == null) return false
//        val otherTypeText = other.name
//        return name == otherTypeText || isConvertible(otherTypeText)
//    }
}

/**
 *
 */
abstract class GlslNamedTypeImpl(node: ASTNode) : GlslNamedElementImpl(node), GlslNamedType {
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

