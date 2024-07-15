package glsl.plugin.reference

import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.psi.*
import com.intellij.util.ProcessingContext
import glsl.plugin.psi.GlslVariable


/**
 *
 */
class GlslReferenceContributor : PsiReferenceContributor() {
    /**
    *
    */
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        val identifierPattern = psiElement(GlslVariable::class.java)
        registrar.registerReferenceProvider(identifierPattern, GlslReferenceProvider())
    }

    /**
     *
     */
    inner class GlslReferenceProvider : PsiReferenceProvider() {
        override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
            if (element !is GlslVariable) return emptyArray()
            return arrayOf(GlslVariableReference(element, element.textRangeInParent))
        }
    }
}


