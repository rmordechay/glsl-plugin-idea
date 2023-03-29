package glsl.plugin.language

import com.intellij.lexer.FlexAdapter
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.tree.IElementType
import glsl.GlslTypes
import glsl.GlslTypes.LEFT_ANGLE
import glsl.GlslTypes.STRING_LITERAL
import glsl._GlslLexer
import glsl.plugin.utils.GlslUtils
import glsl.psi.interfaces.GlslExternalDeclaration

class GlslLexerAdapter : FlexAdapter(_GlslLexer(null)) {
    override fun getTokenType(): IElementType? {
        val currentToken = super.getTokenType()
        if (currentToken == GlslTypes.PP_INCLUDE) {
            val firstPosition = currentPosition
            resolveInclude()
            restore(firstPosition)
        }
        return currentToken
    }

    /**
     *
     */
    private fun resolveInclude() {
        val project = GlslUtils.getProject() ?: return
        super.advance() // One extra for white space
        super.advance()
        if (listOf(STRING_LITERAL, LEFT_ANGLE).contains(super.getTokenType()).not()) {
            return
        }
        var includePath = tokenText
        if (!isValidIncludePath(includePath)) return
        includePath = includePath.substring(1, includePath.length - 1)
        val virtualFile = FilenameIndex.getVirtualFilesByName(includePath, GlobalSearchScope.allScope(project))
        if (virtualFile.isEmpty()) return
        val psiFile = PsiManager.getInstance(project).findFile(virtualFile.toTypedArray()[0])
        val children = psiFile?.children ?: return
        for (child in children) {
            if (child !is GlslExternalDeclaration) continue
            val typeSpecifier = child.declaration?.singleDeclaration?.getAssociatedType()?.getTypeText() ?: continue
            (flex as? _GlslLexer)?.userTypesTable?.add(typeSpecifier)
        }
    }

    /**
     *
     */
    private fun isValidIncludePath(path: String): Boolean {
        val first = path.first()
        val last = path.last()
        return (first == '"' && last == '"') || (first == '<' && last == '>')
    }
}