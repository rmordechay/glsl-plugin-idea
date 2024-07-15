package glsl.plugin.psi.named

import com.intellij.lang.ASTNode
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement
import glsl.psi.interfaces.*
import javax.swing.Icon

/**
 *
 */
interface GlslNamedType : GlslNamedElement {
    fun getStructMembers(): List<GlslNamedVariable>
    fun getStructMember(memberName: String): GlslNamedVariable?
    fun isConvertible(other: String): Boolean
    fun getDimension(): Int
    fun getBinaryExprType(rightExprType: GlslNamedType?, expr: PsiElement? = null): GlslNamedType?

    /**
     *
     */
    fun isEqual(other: GlslNamedType?): Boolean {
        if (other == null) return false
        val otherTypeText = other.name ?: return false
        return name == otherTypeText || isConvertible(otherTypeText)
    }

    /**
     *
     */
    fun isEqual(other: String?): Boolean {
        if (other == null) return false
        return name == other || isConvertible(other)
    }
}

/**
 *
 */
abstract class GlslNamedTypeImpl(node: ASTNode) : GlslNamedElementImpl(node), GlslNamedType


/**
 * type_specifier
 */
abstract class GlslNamedStructSpecifier(node: ASTNode) : GlslNamedTypeImpl(node) {

    /**
     *
     */
    override fun getSelf(): GlslStructSpecifier {
        return this as GlslStructSpecifier
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
    override fun getNameIdentifier(): GlslTypeName? {
        return getSelf().typeName
    }

    /**
     *
     */
    override fun getStructMembers(): List<GlslNamedVariable> {
        val structDeclarationList = getSelf().structDeclarationList
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
        val structDeclarationList = getSelf().structDeclarationList
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
abstract class GlslNamedBlockStructure(node: ASTNode) : GlslNamedTypeImpl(node) {

    /**
     *
     */
    override fun getSelf(): GlslBlockStructure {
        return this as GlslBlockStructure
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
    override fun getNameIdentifier(): GlslTypeName {
        return getSelf().typeName
    }

    /**
     *
     */
    override fun getStructMembers(): List<GlslNamedVariable> {
        val structDeclarationList = getSelf().structDeclarationList
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
        val structDeclarationList = getSelf().structDeclarationList
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