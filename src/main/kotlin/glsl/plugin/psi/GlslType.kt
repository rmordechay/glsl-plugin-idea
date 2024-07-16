package glsl.plugin.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceService
import com.intellij.psi.util.PsiTreeUtil
import glsl.plugin.language.GlslFile
import glsl.plugin.language.GlslFileType
import glsl.plugin.psi.named.GlslNamedVariable
import glsl.plugin.psi.named.types.GlslNamedStructSpecifier
import glsl.plugin.reference.GlslTypeReference
import glsl.plugin.utils.GlslUtils

/**
 *
 */
abstract class GlslType(node: ASTNode) : ASTWrapperPsiElement(node), GlslIdentifier {
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

//    /**
//     *
//     */
//    open fun getStructMembers(): List<GlslNamedVariable> {
//        val declaration = getDeclaration()
//        if (declaration != null) {
//            return (declaration as? GlslType)?.getStructMembers() ?: emptyList()
//        }
//        val resolve = reference?.resolve() as? GlslNamedStructSpecifier
//        return resolve?.getStructMembers() ?: emptyList()
//    }
//
//    /**
//     *
//     */
//    open fun getStructMember(memberName: String): GlslNamedVariable? {
//        return getStructMembers().find { it.name == memberName }
//    }
//
//    /**
//     *
//     */
//    open fun isConvertible(other: String): Boolean {
//        return false
//    }
//
//    /**
//     *
//     */
//    open fun getDimension(): Int {
//        return -1
//    }

    /**
     *
     */
    fun isEmpty(): Boolean {
        return node.text == "IntellijIdeaRulezzz"
    }
}
