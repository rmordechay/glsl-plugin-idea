package glsl.plugin.psi.builtins

import com.intellij.lang.ASTNode
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement
import glsl.data.GlslDefinitions
import glsl.plugin.psi.named.GlslNamedElement
import glsl.plugin.psi.named.GlslNamedVariable
import javax.swing.Icon

/**
 *
 */
abstract class GlslMatrix(node: ASTNode) : GlslBuiltinType(node) {

    /**
     *
     */
    override fun getPsi(): GlslNamedElement {
        TODO("Not yet implemented")
    }

    /**
     *
     */
    override fun getStructMembers(): List<GlslNamedVariable> {
//        val lengthFunc = GlslBuiltinUtils.getVecComponent("length") ?: return emptyList()
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
        val implicitConversions = GlslDefinitions.MATRICES[name]
        return implicitConversions?.contains(other) ?: false
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
    override fun getHighlightTextAttr(): TextAttributesKey {
        TODO("Not yet implemented")
    }

    /**
     *
     */
    override fun getLookupIcon(): Icon? {
        TODO("Not yet implemented")
    }

    /**
     *
     */
    override fun getNameIdentifier(): PsiElement? {
        TODO("Not yet implemented")
    }

    /**
     *
     */
    private fun getMatrixComponentType(): String {
        val typeText = name?.first()
        return when (typeText) {
            'm', 'f' -> "float"
            'd' -> "double"
            else -> ""
        }
    }
}



