package glsl.plugin.reference

import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.patterns.StandardPatterns
import com.intellij.psi.*
import com.intellij.util.ProcessingContext
import glsl.GlslTypes
import glsl.plugin.psi.GlslVariable


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
            val range = TextRange(0, element.name.length)
            return arrayOf(GlslReference(element, range))
        }
    }
}


