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
import glsl.plugin.utils.GlslUtils.isShaderFile
import glsl.psi.interfaces.GlslTypeName
import glsl.psi.interfaces.GlslVariableIdentifier


/**
 *
 */
interface GlslIdentifier: ContributedReferenceHost {

    /**
     *
     */
    override fun getReference(): GlslReference?

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
    fun isNamedElement(): Boolean {
        return parent is GlslNamedElement
    }
}

/**
 *
 */
abstract class GlslVariable(node: ASTNode) : ASTWrapperPsiElement(node), GlslIdentifier {
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
        if (!isShaderFile(this)) return this
        if (newName == null ) return this
        val dummyDeclaration = "void $newName;"
        val dummyElement = (PsiFileFactory
            .getInstance(project)
            .createFileFromText("dummy.glsl", GlslFileType(), dummyDeclaration) as GlslFile)
            .firstChild
        val newIdentifierNode = PsiTreeUtil.findChildOfType(dummyElement, GlslIdentifier::class.java)
        val glslIdentifier: GlslIdentifier = if (newIdentifierNode != null) replace(newIdentifierNode) as GlslIdentifier else this
        return glslIdentifier
    }

//    /**

//    *
//    */
//    override fun getAsNamedElement(): GlslNamedElementImpl? {
//        if (parent is GlslNamedVariable) return parent as GlslNamedVariableImpl
//        if (parent is GlslTypeName) return parent as GlslNamedTypeImpl
//        return null
//    }
    /**
     *
     */
    fun isEmpty(): Boolean {
        return node.text == "IntellijIdeaRulezzz"
    }

}

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
        if (!isShaderFile(this)) return this
        if (newName == null ) return this
        val dummyDeclaration = "$newName;"
        val dummyElement = (PsiFileFactory
            .getInstance(project)
            .createFileFromText("dummy.glsl", GlslFileType(), dummyDeclaration) as GlslFile)
            .firstChild
        val newIdentifierNode = PsiTreeUtil.findChildOfType(dummyElement, GlslIdentifier::class.java)
        val glslIdentifier: GlslIdentifier = if (newIdentifierNode != null) replace(newIdentifierNode) as GlslIdentifier else this
        return glslIdentifier
    }

//    /**
//    *
//    */
//    override fun getAsNamedElement(): GlslNamedElementImpl? {
//        if (parent is GlslNamedVariable) return parent as GlslNamedVariableImpl
//        if (parent is GlslTypeName) return parent as GlslNamedTypeImpl
//        return null
//    }

    /**
     *
     */
    fun isEmpty(): Boolean {
        return node.text == "IntellijIdeaRulezzz"
    }
}
