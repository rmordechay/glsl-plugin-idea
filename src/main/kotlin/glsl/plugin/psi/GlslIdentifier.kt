package glsl.plugin.psi

import com.intellij.psi.ContributedReferenceHost
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.util.PsiTreeUtil
import glsl.plugin.language.GlslFile
import glsl.plugin.language.GlslFileType
import glsl.plugin.psi.named.GlslNamedElement
import glsl.plugin.reference.GlslReference


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

    /**
     *
     */
    fun getIdentifierFromFile(dummyDeclaration: String): GlslIdentifier {
        val dummyElement = (PsiFileFactory.getInstance(project)
            .createFileFromText("dummy.glsl", GlslFileType(), dummyDeclaration) as GlslFile)
            .firstChild
        val newIdentifierNode = PsiTreeUtil.findChildOfType(dummyElement, GlslIdentifier::class.java)
        val glslIdentifier: GlslIdentifier =
            if (newIdentifierNode != null) replace(newIdentifierNode) as GlslIdentifier else this
        return glslIdentifier
    }
}

