package glsl.plugin.psi.named

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.icons.AllIcons
import com.intellij.lang.ASTNode
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.util.childrenOfType
import com.intellij.psi.util.parentOfType
import glsl.plugin.code.highlighting.GlslTextAttributes.FUNC_PARAM_TEXT_ATTR
import glsl.plugin.code.highlighting.GlslTextAttributes.FUNC_TEXT_ATTR
import glsl.plugin.code.highlighting.GlslTextAttributes.MACRO_FUNC_NAME_ATTR
import glsl.plugin.code.highlighting.GlslTextAttributes.MACRO_OBJECT_NAME_ATTR
import glsl.plugin.code.highlighting.GlslTextAttributes.VARIABLE_TEXT_ATTR
import glsl.plugin.psi.GlslType
import glsl.plugin.psi.builtins.GlslBuiltinType
import glsl.plugin.psi.builtins.GlslMatrix
import glsl.plugin.psi.builtins.GlslScalar
import glsl.plugin.psi.builtins.GlslVector
import glsl.plugin.utils.GlslUtils
import glsl.psi.impl.GlslTypeNameImpl
import glsl.psi.interfaces.*
import javax.swing.Icon

/**
 *
 */
interface GlslNamedVariable : GlslNamedElement {
    /**
     *
     */
    fun getAssociatedType(): GlslType?

    /**
     *
     */
    fun getType(typeSpecifier: GlslTypeSpecifier): GlslType? {
        if (typeSpecifier.builtinTypeScalar != null) {
            return typeSpecifier.builtinTypeScalar as GlslScalar
        } else if (typeSpecifier.builtinTypeVector != null) {
            return typeSpecifier.builtinTypeVector as GlslVector
        } else if (typeSpecifier.builtinTypeMatrix != null) {
            return typeSpecifier.builtinTypeMatrix as GlslMatrix
        } else if (typeSpecifier.builtinTypeRest != null) {
            return typeSpecifier.builtinTypeRest as GlslBuiltinType
        } else if (typeSpecifier.structSpecifier != null) {
            return typeSpecifier.structSpecifier as GlslNamedStructSpecifier
        } else if (typeSpecifier.typeName != null) {
            return typeSpecifier.typeName as GlslTypeNameImpl
        }
        return null
    }
}

/**
 *
 */
abstract class GlslNamedVariableImpl(node: ASTNode) : GlslNamedElementImpl(node), GlslNamedVariable

/**
 *
 */
abstract class GlslNamedSingleDeclaration(node: ASTNode) : GlslNamedVariableImpl(node) {

    /**
     *
     */
    override fun getPsi(): GlslSingleDeclaration {
        return this as GlslSingleDeclaration
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
    override fun getLookupIcon(): Icon {
        return AllIcons.Nodes.Variable
    }

    /**
     *
     */
    override fun getAssociatedType(): GlslType? {
        val typeSpecifier = getPsi().typeSpecifier
        return getType(typeSpecifier)
    }

    /**
     *
     */
    override fun getHighlightTextAttr(): TextAttributesKey {
        return VARIABLE_TEXT_ATTR
    }
}

/**
 *
 */
abstract class GlslNamedInitDeclaratorVariable(node: ASTNode) : GlslNamedVariableImpl(node) {

    /**
     *
     */
    override fun getPsi(): GlslInitDeclaratorVariable {
        return this as GlslInitDeclaratorVariable
    }

    /**
     *
     */
    override fun getLookupIcon(): Icon {
        return AllIcons.Nodes.Variable
    }

    /**
     *
     */
    override fun getAssociatedType(): GlslType? {
//        val declaration = getPsi().parent as GlslDeclaration
        return null
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
    override fun getHighlightTextAttr(): TextAttributesKey {
        return VARIABLE_TEXT_ATTR
    }
}

/**
 *
 */
abstract class GlslNamedBlockStructureWrapper(node: ASTNode) : GlslNamedVariableImpl(node) {

    /**
     *
     */
    override fun getPsi(): GlslBlockStructureWrapper {
        return this as GlslBlockStructureWrapper
    }

    /**
     *
     */
    override fun getLookupIcon(): Icon? {
        return null
    }

    /**
     *
     */
    override fun getAssociatedType(): GlslType? {
        return getPsi().blockStructure.typeName as GlslType
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
    override fun getHighlightTextAttr(): TextAttributesKey {
        return VARIABLE_TEXT_ATTR
    }
}

/**
 *
 */
abstract class GlslNamedDeclarationIdentifierWrapper(node: ASTNode) : GlslNamedVariableImpl(node) {

    /**
     *
     */
    override fun getPsi(): GlslDeclarationIdentifierWrapper {
        return this as GlslDeclarationIdentifierWrapper
    }

    /**
     *
     */
    override fun getLookupIcon(): Icon? {
        return null
    }

    /**
     *
     */
    override fun getAssociatedType(): GlslType? {
        return null
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
    override fun getHighlightTextAttr(): TextAttributesKey {
        return VARIABLE_TEXT_ATTR
    }


}


/**
 *
 */
abstract class GlslNamedStructDeclarator(node: ASTNode) : GlslNamedVariableImpl(node) {

    /**
     *
     */
    override fun getPsi(): GlslStructDeclarator {
        return this as GlslStructDeclarator
    }

    /**
     *
     */
    override fun getLookupIcon(): Icon? {
        return AllIcons.Nodes.Field
    }

    /**
     *
     */
    override fun getAssociatedType(): GlslType? {
        val structDeclaration = getPsi().parent as GlslStructDeclaration
        val typeSpecifier = structDeclaration.typeSpecifier ?: return null
        return getType(typeSpecifier)
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
    override fun getHighlightTextAttr(): TextAttributesKey {
        return VARIABLE_TEXT_ATTR
    }

    /**
     *
     */
    override fun getLookupElement(returnTypeText: String?): LookupElement? {
        val name = getPsi().name ?: return null
        val typeText = getAssociatedType()?.text
        return GlslUtils.createLookupElement(name, icon = getLookupIcon(), returnTypeText = typeText)
    }
}

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
    override fun getAssociatedType(): GlslType? {
        return getType(getPsi().typeSpecifier)
    }

    /**
     *
     */
    override fun getHighlightTextAttr(): TextAttributesKey {
        return FUNC_TEXT_ATTR
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

/**
 *
 */
abstract class GlslNamedParameterDeclarator(node: ASTNode) : GlslNamedVariableImpl(node) {
    /**
     *
     */
    override fun getPsi(): GlslParameterDeclarator {
        return this as GlslParameterDeclarator
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
    override fun getAssociatedType(): GlslType? {
        return getType(getPsi().typeSpecifier)
    }

    /**
     *
     */
    override fun getLookupIcon(): Icon {
        return AllIcons.Nodes.Parameter
    }

    /**
     *
     */
    override fun getHighlightTextAttr(): TextAttributesKey {
        return FUNC_PARAM_TEXT_ATTR
    }
}

/**
 *
 */
abstract class GlslNamedPpDefineObject(node: ASTNode) : GlslNamedVariableImpl(node) {
    /**
     *
     */
    override fun getPsi(): GlslPpDefineObject {
        return this as GlslPpDefineObject
    }

    /**
     *
     */
    override fun getNameIdentifier(): GlslVariableIdentifier? {
        return getPsi().childrenOfType<GlslVariableIdentifier>().firstOrNull()
    }

    /**
     *
     */
    override fun getAssociatedType(): GlslType? {
        return null
    }

    /**
     *
     */
    override fun getLookupIcon(): Icon {
        return AllIcons.Nodes.Variable
    }

    /**
     *
     */
    override fun getHighlightTextAttr(): TextAttributesKey {
        return MACRO_OBJECT_NAME_ATTR
    }
}

/**
 *
 */
abstract class GlslNamedPpDefineFunction(node: ASTNode) : GlslNamedVariableImpl(node) {
    /**
     *
     */
    override fun getPsi(): GlslPpDefineFunction {
        return this as GlslPpDefineFunction
    }

    /**
     *
     */
    override fun getNameIdentifier(): GlslVariableIdentifier? {
        return getPsi().childrenOfType<GlslVariableIdentifier>().firstOrNull()
    }

    /**
     *
     */
    override fun getAssociatedType(): GlslType? {
        return null
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
    override fun getHighlightTextAttr(): TextAttributesKey {
        return MACRO_FUNC_NAME_ATTR
    }
}

