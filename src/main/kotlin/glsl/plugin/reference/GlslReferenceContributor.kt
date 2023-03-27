package glsl.plugin.reference

import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.patterns.StandardPatterns
import com.intellij.patterns.StandardPatterns.or
import com.intellij.psi.*
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet
import com.intellij.util.ProcessingContext
import glsl.GlslTypes
import glsl.plugin.psi.GlslIdentifierImpl
import glsl.psi.interfaces.GlslIncludePath


/**
 *
 */
class GlslReferenceContributor : PsiReferenceContributor() {
    private val numeric = StandardPatterns.or(
        psiElement(GlslTypes.INTCONSTANT),
        psiElement(GlslTypes.UINTCONSTANT),
        psiElement(GlslTypes.FLOATCONSTANT),
        psiElement(GlslTypes.DOUBLECONSTANT),
    )

    private val identifierPattern = psiElement(GlslIdentifierImpl::class.java)
        .andNot(psiElement().afterSibling(psiElement(GlslTypes.TYPE_QUALIFIER)))
        .andNot(psiElement().afterSibling(psiElement(GlslTypes.TYPE_SPECIFIER)))
        .andNot(psiElement().afterLeaf("struct"))
        .andNot(psiElement().afterLeaf(numeric))

    private val includePattern = psiElement(GlslIncludePath::class.java)

    /**
     *
     */
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(or(identifierPattern, includePattern), GlslReferenceProvider())
    }

    /**
     *
     */
    inner class GlslReferenceProvider : PsiReferenceProvider() {

        override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<out PsiReference> {
            if (element is GlslIdentifierImpl) {
                val range = TextRange(0, element.name.length)
                return arrayOf(GlslReference(element, range))
            } else if (element is GlslIncludePath) {
                val path = element.text.replace("\"", "")
                return GlslFileReferenceSet(path, element, this).allReferences
            }
            return PsiReference.EMPTY_ARRAY
        }
    }

    inner class GlslFileReferenceSet(path: String, element: PsiElement, provider: PsiReferenceProvider?)
        : FileReferenceSet(path, element, 0, provider, true) {

        override fun createFileReference(range: TextRange?, index: Int, text: String): FileReference? {
            return GlslFileReference(this, range, index, text)
        }
    }

    inner class GlslFileReference(fileReferenceSet: FileReferenceSet, range: TextRange?, index: Int, text: String) : FileReference(fileReferenceSet, range, index, text) {
        override fun handleElementRename(newElementName: String): PsiElement {
            return super.handleElementRename(newElementName)
        }
    }
}


