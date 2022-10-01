package glsl.plugin.psi.named

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.icons.AllIcons
import com.intellij.lang.ASTNode
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.util.parentOfType
import glsl.plugin.code.highlighting.GlslTextAttributes.FUNC_PARAM_TEXT_ATTR
import glsl.plugin.code.highlighting.GlslTextAttributes.FUNC_TEXT_ATTR
import glsl.plugin.code.highlighting.GlslTextAttributes.VARIABLE_TEXT_ATTR
import glsl.plugin.psi.GlslType
import glsl.plugin.utils.GlslUtils
import glsl.psi.interfaces.*
import javax.swing.Icon

/**
 *
 */
interface GlslNamedIdentifier : GlslNamedElement

/**
 *
 */
abstract class GlslNamedIdentifierImpl(node: ASTNode) : GlslNamedElementImpl(node), GlslNamedIdentifier

/**
 *
 */
abstract class GlslNamedSingleDeclaration(node: ASTNode) : GlslNamedIdentifierImpl(node) {

    /**
     *
     */
    override fun getSelf(): GlslSingleDeclaration {
        return this as GlslSingleDeclaration
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
        return GlslType.getInstance(getSelf().typeSpecifier)
    }

    /**
     *
     */
    override fun getNameIdentifier(): GlslVariableIdentifier? {
        return getSelf().variableIdentifier
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
abstract class GlslNamedInitDeclaratorVariable(node: ASTNode) : GlslNamedIdentifierImpl(node) {

    /**
     *
     */
    override fun getSelf(): GlslInitDeclaratorVariable {
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
        val declaration = getSelf().parent as GlslDeclaration
        return GlslType.getInstance(declaration.typeSpecifier)
    }

    /**
     *
     */
    override fun getNameIdentifier(): GlslVariableIdentifier? {
        return getSelf().variableIdentifier
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
abstract class GlslNamedBlockStructureWrapper(node: ASTNode) : GlslNamedIdentifierImpl(node) {

    /**
     *
     */
    override fun getSelf(): GlslBlockStructureWrapper {
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
        return getSelf().blockStructure
    }

    /**
     *
     */
    override fun getNameIdentifier(): GlslVariableIdentifier? {
        return getSelf().variableIdentifier
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
abstract class GlslNamedDeclarationIdentifierWrapper(node: ASTNode) : GlslNamedIdentifierImpl(node) {

    /**
     *
     */
    override fun getSelf(): GlslDeclarationIdentifierWrapper {
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
        return getSelf().variableIdentifier
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
abstract class GlslNamedStructDeclarator(node: ASTNode) : GlslNamedIdentifierImpl(node) {

    /**
     *
     */
    override fun getSelf(): GlslStructDeclarator {
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
        val structDeclaration = getSelf().parent as GlslStructDeclaration
        return GlslType.getInstance(structDeclaration.typeSpecifier)
    }

    /**
     *
     */
    override fun getNameIdentifier(): GlslVariableIdentifier? {
        return getSelf().variableIdentifier
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
        val name = getSelf().name ?: return null
        val typeText = getAssociatedType()?.getTypeText()
        return GlslUtils.createLookupElement(name, icon = getLookupIcon(), returnTypeText = typeText)
    }
}

/**
 *
 */
abstract class GlslNamedFunctionHeader(node: ASTNode) : GlslNamedIdentifierImpl(node) {

    /**
     *
     */
    override fun getSelf(): GlslFunctionHeader {
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
        return getSelf().variableIdentifier
    }

    /**
     *
     */
    override fun getAssociatedType(): GlslType? {
        return GlslType.getInstance(getSelf().typeSpecifier)
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
        val functionPrototype = getSelf().parentOfType<GlslFunctionPrototype>() ?: return null
        return GlslUtils.getFunctionLookupElement(functionPrototype, getLookupIcon())
    }

    /**
     *
     */
    fun getParameterDeclarators(): List<GlslNamedElement> {
        val funcPrototype = getSelf().parent as GlslFunctionPrototype? ?: return emptyList()
        return funcPrototype.funcHeaderWithParams?.parameterDeclaratorList ?: emptyList()
    }
}

/**
 *
 */
abstract class GlslNamedParameterDeclarator(node: ASTNode) : GlslNamedIdentifierImpl(node) {
    /**
     *
     */
    override fun getSelf(): GlslParameterDeclarator {
        return this as GlslParameterDeclarator
    }

    /**
     *
     */
    override fun getNameIdentifier(): GlslVariableIdentifier? {
        return getSelf().variableIdentifier
    }

    /**
     *
     */
    override fun getAssociatedType(): GlslType? {
        return GlslType.getInstance(getSelf().typeSpecifier)
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
abstract class GlslNamedPpSingleDeclaration(node: ASTNode) : GlslNamedIdentifierImpl(node) {

    /**
     *
     */
    override fun getSelf(): GlslPpSingleDeclaration {
        return this as GlslPpSingleDeclaration
    }

    /**
     *
     */
    override fun getNameIdentifier(): GlslVariableIdentifier? {
        return getSelf().variableIdentifier
    }

    /**
     *
     */
    override fun getAssociatedType(): GlslType? {
        return getSelf().expr?.getExprType()
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
        return VARIABLE_TEXT_ATTR
    }
}
/**
 *
 */
abstract class GlslNamedPpDefineFunction(node: ASTNode) : GlslNamedIdentifierImpl(node) {

    /**
     *
     */
    override fun getSelf(): GlslPpDefineFunction {
        return this as GlslPpDefineFunction
    }

    /**
     *
     */
    override fun getNameIdentifier(): GlslVariableIdentifier? {
        return getSelf().variableIdentifier
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
        return FUNC_TEXT_ATTR
    }
}
/**
 *
 */
abstract class GlslNamedPpDefineParam(node: ASTNode) : GlslNamedIdentifierImpl(node) {

    /**
     *
     */
    override fun getSelf(): GlslPpDefineParam {
        return this as GlslPpDefineParam
    }

    /**
     *
     */
    override fun getNameIdentifier(): GlslVariableIdentifier? {
        return getSelf().variableIdentifier
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
        return AllIcons.Nodes.Parameter
    }

    /**
     *
     */
    override fun getHighlightTextAttr(): TextAttributesKey {
        return FUNC_PARAM_TEXT_ATTR
    }
}
