package glsl.plugin.psi.named.types.user

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.icons.AllIcons
import com.intellij.lang.ASTNode
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.tree.IElementType
import glsl.GlslTypes
import glsl.plugin.code.highlighting.GlslTextAttributes
import glsl.plugin.psi.named.GlslNamedElement
import glsl.plugin.psi.named.GlslNamedType
import glsl.plugin.psi.named.GlslNamedTypeImpl
import glsl.plugin.psi.named.GlslNamedVariable
import glsl.psi.interfaces.GlslStructDeclarator
import glsl.psi.interfaces.GlslStructSpecifier
import glsl.psi.interfaces.GlslTypeName
import javax.swing.Icon

/**
 * type_specifier
 */
abstract class GlslNamedStructSpecifier(node: ASTNode) : GlslNamedTypeImpl(node), GlslNamedType {
    /**
     *
     */
    override fun getName(): String {
        return getPsi().typeName?.getName() ?: ""
    }

    /**
     *
     */
    override fun getPsi(): GlslStructSpecifier {
        return this as GlslStructSpecifier
    }

    /**
     *
     */
    override fun getHighlightTextAttr(): TextAttributesKey {
        return GlslTextAttributes.USER_DEFINED_TYPE_TEXT_ATTR
    }

    /**
     *
     */
    override fun getLookupElement(returnTypeText: String?): LookupElement? {
        TODO("Not yet implemented")
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
    override fun getNameIdentifier(): GlslTypeName? {
        return getPsi().typeName
    }

    /**
     *
     */
    override fun getStructMembers(): List<GlslNamedVariable> {
        val structDeclarationList = getPsi().structDeclarationList
        val structDeclarators = mutableListOf<GlslStructDeclarator>()
        for (structDeclaration in structDeclarationList) {
            for (structDeclarator in structDeclaration.structDeclaratorList) {
                structDeclarators.add(structDeclarator)
            }
        }
        return structDeclarators
    }

    /**
     *
     */
    override fun getStructMember(memberName: String): GlslNamedVariable? {
        val structDeclarationList = getPsi().structDeclarationList
        for (structDeclaration in structDeclarationList) {
            for (structDeclarator in structDeclaration.structDeclaratorList) {
                if (structDeclarator.name != memberName) continue
                return structDeclarator
            }
        }
        return null
    }

    /**
     *
     */
    override fun canCast(other: IElementType?): Boolean {
        return false
    }

    /**
     *
     */
    override fun typeAsToken(): IElementType? {
        return GlslTypes.USER_TYPE_NAME
    }

    /**
     *
     */
    override fun getBinaryType(other: GlslNamedElement?, operation: String): GlslNamedType? {
        return null
    }

    /**
     *
     */
    override fun getDimension(): Int {
        return 1
    }
}