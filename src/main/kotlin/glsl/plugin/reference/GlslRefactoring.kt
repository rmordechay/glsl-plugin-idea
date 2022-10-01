package glsl.plugin.reference

import com.intellij.lang.refactoring.RefactoringSupportProvider
import com.intellij.psi.PsiElement
import glsl.plugin.psi.named.GlslNamedElement



/**
 *
 */
class GlslRefactoring : RefactoringSupportProvider() {
    /**
    *
    */
    override fun isMemberInplaceRenameAvailable(element: PsiElement, context: PsiElement?): Boolean {
        return element is GlslNamedElement
    }
}