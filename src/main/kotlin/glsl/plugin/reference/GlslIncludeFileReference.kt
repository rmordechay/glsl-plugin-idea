package glsl.plugin.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import glsl.plugin.utils.GlslUtils.getPathStringFromInclude
import glsl.plugin.utils.GlslUtils.getVirtualFile
import glsl.psi.interfaces.GlslPpIncludeDeclaration

class GlslIncludeFileReference(
    element: GlslPpIncludeDeclaration,
    rangeInElement: TextRange
) : PsiReferenceBase<GlslPpIncludeDeclaration>(element, rangeInElement) {

    override fun resolve(): PsiElement? {
        val project = element.project
        val baseFile = element.containingFile.virtualFile ?: return null

        val path = getPathStringFromInclude(element) ?: return null
        val vf = getVirtualFile(path, baseFile, project) ?: return null

        return PsiManager.getInstance(project).findFile(vf)
    }

    override fun getVariants(): Array<Any> = emptyArray()
}