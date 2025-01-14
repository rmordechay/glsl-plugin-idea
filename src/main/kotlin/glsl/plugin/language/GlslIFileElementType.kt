package glsl.plugin.language

import com.intellij.lang.ASTNode
import com.intellij.lang.impl.PsiBuilderImpl
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IFileElementType
import com.intellij.testFramework.LightVirtualFileBase
import com.intellij.util.asSafely
import glsl.plugin.utils.GlslUtils.getRealVirtualFile

/**
 *
 */
class GlslIFileElementType : IFileElementType(GlslLanguage.INSTANCE) {
    override fun doParseContents(chameleon: ASTNode, psi: PsiElement): ASTNode? {
        val project = psi.project
        val parserDefinition = GlslParserDefinition()
        val vf = psi.containingFile.viewProvider.virtualFile
        val original = vf.asSafely<LightVirtualFileBase>()?.originalFile ?: vf
        val builder = PsiBuilderImpl(project, parserDefinition, GlslLexer(project, psi.getRealVirtualFile()), chameleon, chameleon.chars)
        val parser = GlslParserAdapter()
        val rootNode = parser.parse(this, builder)
        return rootNode.firstChildNode
    }
}