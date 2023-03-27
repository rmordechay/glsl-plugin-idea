package glsl.plugin.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.ContributedReferenceHost
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceService
import com.intellij.psi.util.PsiTreeUtil
import glsl.plugin.language.GlslFile
import glsl.plugin.language.GlslFileType
import glsl.plugin.psi.named.*
import glsl.plugin.reference.GlslReference
import glsl.psi.interfaces.GlslTypeName
import glsl.psi.interfaces.GlslVariableIdentifier


/**
 *
 */
interface GlslIdentifier: ContributedReferenceHost {

    /**
     *
     */
    fun getAsNamedElement(): GlslNamedElement?

    /**
     *
     */
    fun replaceElementName(newName: String?): GlslIdentifier?

    /**
     *
     */
    fun getName(): String

    /**
     *
     */
    override fun getReference(): GlslReference?
}

/**
 *
 */
abstract class GlslIdentifierImpl(node: ASTNode) : ASTWrapperPsiElement(node), GlslIdentifier {

    /**
    *
    */
    override fun getReferences(): Array<out PsiReference?> {
        return PsiReferenceService.getService().getContributedReferences(this)
    }

    /**
    *
    */
    override fun getReference(): GlslReference? {
        return references.firstOrNull() as? GlslReference
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
        if (newName == null ) return this
        val dummyDeclaration = if (this is GlslVariableIdentifier) {
            "void $newName;"
        } else if (this is GlslTypeName) {
            "struct $newName {int a;};"
        } else {
            return this
        }
        val dummyElement = (PsiFileFactory
            .getInstance(project)
            .createFileFromText("dummy.glsl", GlslFileType(), dummyDeclaration) as GlslFile)
            .firstChild
        val newIdentifierNode = PsiTreeUtil.findChildOfType(dummyElement, GlslIdentifier::class.java)
        return if (newIdentifierNode != null) replace(newIdentifierNode) as GlslIdentifier else this
    }

    /**
    *
    */
    override fun getAsNamedElement(): GlslNamedElementImpl? {
        if (parent is GlslNamedIdentifier) return parent as GlslNamedIdentifierImpl
        if (parent is GlslNamedUserType) return parent as GlslNamedUserTypeImpl
        return null
    }

    /**
     *
     */
    fun isEmpty(): Boolean {
        return node.text == "IntellijIdeaRulezzz"
    }
}

abstract class GlslInclude(node: ASTNode) : ASTWrapperPsiElement(node), ContributedReferenceHost {
    /**
     *
     */
    override fun getReferences(): Array<out PsiReference?> {
        return PsiReferenceService.getService().getContributedReferences(this)
    }

    /**
     *
     */
    override fun getReference(): PsiReference? {
        return references.firstOrNull()
    }
}