package glsl.plugin.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.ContributedReferenceHost
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceService
import glsl.plugin.reference.GlslFileReference

abstract class GlslInclude(node: ASTNode) : ASTWrapperPsiElement(node), ContributedReferenceHost {
    /**
     *
     */
    override fun getReferences(): Array<out PsiReference?> {
        return PsiReferenceService.getService().getContributedReferences(this)
    }

    /**
     *
     */
    override fun getReference(): GlslFileReference? {
        return references.firstOrNull() as GlslFileReference
    }
}