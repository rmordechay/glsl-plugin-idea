package glsl.plugin.reference

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.search.PsiSearchHelper
import com.intellij.psi.search.SearchScope
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.refactoring.rename.RenamePsiElementProcessor
import glsl.plugin.psi.named.GlslNamedElement

/**
 * Processor that adds rename support for GLSL elements inside injected fragments.
 * It is necessary for injected elements that do not live in the Project scope.
 */
class GlslRenameProcessor : RenamePsiElementProcessor() {

    override fun canProcessElement(element: PsiElement): Boolean = element is GlslNamedElement

    override fun findReferences(element: PsiElement, searchScope: SearchScope, searchInCommentsAndStrings: Boolean): Collection<PsiReference> {
        // allows GLSL language renaming in injected fragments outside the project scope (external libraries etc.)
        val injected = InjectedLanguageManager.getInstance(element.project).isInjectedFragment(element.containingFile)
        val scope = if (injected) PsiSearchHelper.getInstance(element.project).getUseScope(element) else searchScope;

        return super.findReferences(element, scope, searchInCommentsAndStrings)
    }
}
