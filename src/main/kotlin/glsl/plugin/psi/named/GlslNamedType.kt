package glsl.plugin.psi.named

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.icons.AllIcons
import com.intellij.lang.ASTNode
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement
import glsl.plugin.code.highlighting.GlslTextAttributes
import glsl.plugin.psi.GlslType
import glsl.psi.interfaces.GlslBlockStructure
import glsl.psi.interfaces.GlslStructDeclarator
import glsl.psi.interfaces.GlslStructSpecifier
import glsl.psi.interfaces.GlslTypeName
import javax.swing.Icon


/**
 *
 */
interface GlslNamedType : GlslNamedElement


/**
 * type_specifier
 */
abstract class GlslNamedStructSpecifier(node: ASTNode) : GlslType(node), GlslNamedType {

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
    override fun setName(name: String): PsiElement {
        TODO("Not yet implemented")
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
    override fun isConvertible(other: String): Boolean {
        TODO("Not yet implemented")
    }

    /**
     *
     */
    override fun getDimension(): Int {
        TODO("Not yet implemented")
    }

    /**
     *
     */
    override fun getBinaryExprType(rightExprType: GlslNamedType?, expr: PsiElement?): GlslNamedType? {
        TODO("Not yet implemented")
    }
}

/**
 * block_structure
 */
abstract class GlslNamedBlockStructure(node: ASTNode) : GlslType(node), GlslNamedType {

    /**
     *
     */
    override fun getName(): String {
        return getPsi().typeName.getName()
    }

    /**
     *
     */
    override fun getPsi(): GlslBlockStructure {
        return this as GlslBlockStructure
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
    override fun setName(name: String): PsiElement {
        TODO("Not yet implemented")
    }

    /**
     *
     */
    override fun getNameIdentifier(): GlslTypeName {
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
    override fun isConvertible(other: String): Boolean {
        TODO("Not yet implemented")
    }

    /**
     *
     */
    override fun getDimension(): Int {
        TODO("Not yet implemented")
    }

    /**
     *
     */
    override fun getBinaryExprType(rightExprType: GlslNamedType?, expr: PsiElement?): GlslNamedType? {
        TODO("Not yet implemented")
    }
}