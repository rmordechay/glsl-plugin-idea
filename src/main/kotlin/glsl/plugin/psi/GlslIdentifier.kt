package glsl.plugin.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.project.Project
import com.intellij.psi.ContributedReferenceHost
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceService
import com.intellij.psi.util.PsiTreeUtil
import glsl.plugin.language.GlslFile
import glsl.plugin.language.GlslFileType
import glsl.plugin.psi.named.*
import glsl.plugin.reference.GlslReference
import glsl.psi.impl.GlslPpDefineDeclarationImpl
import glsl.psi.interfaces.GlslPpDefineDeclaration
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
    fun isEmpty(): Boolean

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
        return replaceName(newName, this, project)
    }

    /**
    *
    */
    override fun getAsNamedElement(): GlslNamedElementImpl? {
        if (parent is GlslNamedIdentifier) return parent as GlslNamedIdentifierImpl
        if (parent is GlslNamedUserType) return parent as GlslNamedUserTypeImpl
        if (parent is GlslPpDefineDeclaration) return parent as GlslPpDefineDeclarationImpl
        return null
    }

    /**
     *
     */
    override fun isEmpty(): Boolean {
        return node.text == "IntellijIdeaRulezzz"
    }

    companion object {

        /**
         *
         */
        fun replaceName(newName: String?, identifier: GlslIdentifier, project: Project) : GlslIdentifier? {
            if (newName == null ) return identifier
            val dummyDeclaration = if (identifier is GlslVariableIdentifier) {
                "void $newName;"
            } else if (identifier is GlslTypeName) {
                "struct $newName {int a;};"
            } else {
                return identifier
            }

            val dummyElement = (PsiFileFactory
                .getInstance(project)
                .createFileFromText("dummy.glsl", GlslFileType(), dummyDeclaration) as GlslFile)
                .firstChild
            val newIdentifierNode = PsiTreeUtil.findChildOfType(dummyElement, GlslIdentifier::class.java)
            return if (newIdentifierNode != null) identifier.replace(newIdentifierNode) as GlslIdentifier else identifier
        }
    }
}

