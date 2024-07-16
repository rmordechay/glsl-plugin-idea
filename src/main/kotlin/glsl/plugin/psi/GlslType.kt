package glsl.plugin.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceService
import com.intellij.psi.util.PsiTreeUtil
import glsl.plugin.language.GlslFile
import glsl.plugin.language.GlslFileType
import glsl.plugin.psi.named.GlslNamedStructSpecifier
import glsl.plugin.psi.named.GlslNamedType
import glsl.plugin.psi.named.GlslNamedVariable
import glsl.plugin.reference.GlslTypeReference
import glsl.plugin.utils.GlslUtils

/**
 *
 */
abstract class GlslType(node: ASTNode) : ASTWrapperPsiElement(node), GlslIdentifier {
    /**
     *
     */
    fun isEqual(other: GlslType?): Boolean {
        if (other == null) return false
        val otherTypeText = other.name
        return name == otherTypeText || isConvertible(otherTypeText)
    }

    /**
     *
     */
    fun isEqual(other: String?): Boolean {
        if (other == null) return false
        return name == other || isConvertible(other)
    }

    /**
     *
     */
    override fun getReferences(): Array<out PsiReference?> {
        return PsiReferenceService.getService().getContributedReferences(this)
    }

    /**
     *
     */
    override fun getReference(): GlslTypeReference? {
        return references.firstOrNull() as? GlslTypeReference
    }

    /**
     *
     */
    override fun getName(): String {
        return node.text.replace("IntellijIdeaRulezzz", "")
    }

    /**
     *
     */
    override fun replaceElementName(newName: String?): GlslIdentifier? {
        if (!GlslUtils.isShaderFile(this)) return this
        if (newName == null) return this
        val dummyDeclaration = "$newName;"
        val dummyElement = (PsiFileFactory.getInstance(project)
            .createFileFromText("dummy.glsl", GlslFileType(), dummyDeclaration) as GlslFile)
            .firstChild
        val newIdentifierNode = PsiTreeUtil.findChildOfType(dummyElement, GlslType::class.java)
        return if (newIdentifierNode != null) replace(newIdentifierNode) as GlslIdentifier else this
    }

    /**
     *
     */
    fun isEmpty(): Boolean {
        return node.text == "IntellijIdeaRulezzz"
    }

    /**
     *
     */
    open fun getStructMembers(): List<GlslNamedVariable> {
        val resolve = reference?.resolve() as? GlslNamedStructSpecifier
        return resolve?.getStructMembers() ?: emptyList()
    }

    /**
     *
     */
    open fun getStructMember(memberName: String): GlslNamedVariable? {
        return getStructMembers().find { it.name == memberName }
    }

    /**
     *
     */
    open fun isConvertible(other: String): Boolean {
        return false
    }

    /**
     *
     */
    open fun getDimension(): Int {
        return -1
    }

    /**
     *
     */
    open fun getBinaryExprType(rightExprType: GlslNamedType?, expr: PsiElement? = null): GlslNamedType? {
        return null
    }
}
