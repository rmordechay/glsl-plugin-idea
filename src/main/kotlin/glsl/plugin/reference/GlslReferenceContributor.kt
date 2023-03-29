package glsl.plugin.reference

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.patterns.StandardPatterns.or
import com.intellij.psi.*
import com.intellij.util.ProcessingContext
import glsl.GlslTypes
import glsl.plugin.psi.GlslIdentifierImpl
import glsl.plugin.utils.GlslUtils.isValidIncludePath
import glsl.psi.interfaces.GlslPpIncludePath


/**
 *
 */
class GlslReferenceContributor : PsiReferenceContributor() {
    private val numeric = or(
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

    private val includePattern = psiElement(GlslPpIncludePath::class.java)

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
        private var project: Project? = null

        override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<out PsiReference> {
            if (project == null) {
                project = element.project
            }
            if (element is GlslIdentifierImpl) {
                val range = TextRange(0, element.name.length)
                return arrayOf(GlslReference(element, range))
            } else if (element is GlslPpIncludePath) {
                val includePath = element.text
                if (!isValidIncludePath(includePath)) return PsiReference.EMPTY_ARRAY
                val path =  includePath.substring(1, includePath.length - 1)
                return GlslFileReferenceSet(path, element, this).allReferences
            }
            return PsiReference.EMPTY_ARRAY
        }
    }

}


