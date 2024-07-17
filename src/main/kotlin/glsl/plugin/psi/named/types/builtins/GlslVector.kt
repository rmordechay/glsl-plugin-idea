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
import glsl.plugin.utils.GlslBuiltinUtils
import glsl.psi.interfaces.GlslBuiltinTypeVector
import javax.swing.Icon


/**
 *
 */
abstract class GlslVector(node: ASTNode) : GlslNamedTypeImpl(node), GlslBuiltinType {

    /**
     *
     */
    override fun getPsi(): GlslBuiltinTypeVector {
        return this as GlslBuiltinTypeVector
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
        val vectorComponents = GlslBuiltinUtils.getVecStructs()[name]?.values?.mapNotNull { it as? GlslNamedVariable }
        return vectorComponents ?: emptyList()
    }

    /**
     *
     */
    override fun getStructMember(memberName: String): GlslNamedVariable? {
        return getStructMembers().find { it.name == memberName }
    }

    /**
     *
     */
    override fun getDimension(): Int {
        val lastChar = name?.last()
        return when (lastChar) {
            '2' -> 2
            '3' -> 3
            '4' -> 4
            else -> 0
        }
    }

    /**
     *
     */
    override fun isConvertible(other: String): Boolean {
        val implicitConversions = GlslDefinitions.VECTORS[name]
        return implicitConversions?.contains(other) ?: false
    }

    /**
     *
     */
    private fun getVectorComponentType(): String {
        val typeText = name?.first()
        return when (typeText) {
            'v', 'f' -> "float"
            'd' -> "double"
            'u' -> "uint"
            'i' -> "int"
            'b' -> "bool"
            else -> ""
        }
    }

}
