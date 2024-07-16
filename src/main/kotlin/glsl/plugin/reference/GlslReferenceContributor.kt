package glsl.plugin.reference

import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.psi.*
import com.intellij.util.ProcessingContext
import glsl.plugin.psi.GlslType
import glsl.plugin.psi.GlslVariable


/**
 *
 */
class GlslReferenceContributor : PsiReferenceContributor() {
    /**
    *
    */
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        val variablePattern = psiElement(GlslVariable::class.java)
        val typePattern = psiElement(GlslType::class.java)
        registrar.registerReferenceProvider(variablePattern, GlslVariableReferenceProvider())
        registrar.registerReferenceProvider(typePattern, GlslTypeReferenceProvider())
    }

    /**
     *
     */
    inner class GlslVariableReferenceProvider : PsiReferenceProvider() {
        override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
            if (element !is GlslVariable) return emptyArray()
            return arrayOf(GlslVariableReference(element, element.textRangeInParent))
        }
    }

    /**
     *
     */
    inner class GlslTypeReferenceProvider : PsiReferenceProvider() {
        override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
            if (element !is GlslType) return emptyArray()
            return arrayOf(GlslTypeReference(element, element.textRangeInParent))
        }
    }
}


