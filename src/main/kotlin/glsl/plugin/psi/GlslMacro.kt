package glsl.plugin.psi

import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceService
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.tree.IElementType
import glsl.GlslTypes
import glsl.plugin.psi.named.GlslNamedElement
import glsl.plugin.reference.GlslReference


/**
 *
 */
class GlslMacro(type: IElementType, text: CharSequence) : LeafPsiElement(type, text), GlslIdentifier {

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
    override fun getAsNamedElement(): GlslNamedElement? {
        return null
    }

    /**
     *
     */
    override fun replaceElementName(newName: String?): GlslIdentifier {
        if (newName == null) return this
        return GlslMacro(GlslTypes.MACRO_EXPANSION, newName)
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
    override fun isEmpty(): Boolean {
        return node.text == "IntellijIdeaRulezzz"
    }
}

