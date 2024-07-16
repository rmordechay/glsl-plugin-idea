package glsl.plugin.psi

import com.intellij.psi.ContributedReferenceHost
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
    fun getDeclaration(): GlslNamedElement? {
        return parent as? GlslNamedElement
    }
}

