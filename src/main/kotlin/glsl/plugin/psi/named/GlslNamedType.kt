package glsl.plugin.psi.named

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
interface GlslNamedUserType : GlslNamedElement, GlslType


/**
 *
 */
abstract class GlslNamedUserTypeImpl(node: ASTNode) : GlslNamedElementImpl(node), GlslNamedUserType {

    /**
    *
    */
    override fun getAssociatedType(): GlslType? {
        return this
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
    override fun getHighlightTextAttr(): TextAttributesKey {
        return GlslTextAttributes.USER_DEFINED_TYPE_TEXT_ATTR
    }

    /**
    *
    */
    override fun getTypeText(): String? {
        return name
    }

    /**
     *
     */
    override fun isConvertible(other: String): Boolean {
        return false
    }

    /**
     *
     */
    override fun getDimension(): Int {
        return 1
    }


    /**
    *
    */
    override fun getBinaryExprType(rightExprType: GlslType, expr: PsiElement?): GlslType? {
        return null
    }

}

/**
 * type_specifier
 */
abstract class GlslNamedStructSpecifier(node: ASTNode) : GlslNamedUserTypeImpl(node) {

    /**
    *
    */
    override fun getSelf(): GlslStructSpecifier {
        return this as GlslStructSpecifier
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
    override fun getStructMembers(): List<GlslNamedElement> {
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
    override fun getStructMember(memberName: String): GlslNamedElement? {
        val structDeclarationList = getSelf().structDeclarationList
        for (structDeclaration in structDeclarationList) {
            for (structDeclarator in structDeclaration.structDeclaratorList) {
                if (structDeclarator.name != memberName) continue
                return structDeclarator
            }
        }
        return null
    }
}

/**
 * block_structure
 */
abstract class GlslNamedBlockStructure(node: ASTNode) : GlslNamedUserTypeImpl(node) {

    /**
    *
    */
    override fun getSelf(): GlslBlockStructure {
        return this as GlslBlockStructure
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
    override fun getStructMembers(): List<GlslNamedElement> {
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
    override fun getStructMember(memberName: String): GlslNamedElement? {
        val structDeclarationList = getSelf().structDeclarationList
        for (structDeclaration in structDeclarationList) {
            for (structDeclarator in structDeclaration.structDeclaratorList) {
                if (structDeclarator.name != memberName) continue
                return structDeclarator
            }
        }
        return null
    }
}