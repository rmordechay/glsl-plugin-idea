package glsl.plugin.language

import com.intellij.lexer.FlexAdapter
import com.intellij.psi.tree.IElementType
import glsl.GlslTypes
import glsl._GlslLexer
import glsl.plugin.utils.GlslUtils
import glsl.plugin.utils.GlslUtils.isValidIncludePath
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
        super.advance() // One extra for white space
        super.advance()
        var includePath = tokenText
        if (!isValidIncludePath(includePath)) return
        includePath = includePath.substring(1, includePath.length - 1)
        val psiFile = GlslUtils.getPsiFileByPath(includePath)
        val children = psiFile?.children ?: return
        for (child in children) {
            if (child !is GlslExternalDeclaration) continue
            val typeSpecifier = child.declaration?.singleDeclaration?.getAssociatedType()?.getTypeText() ?: continue
            (flex as? _GlslLexer)?.userTypesTable?.add(typeSpecifier)
        }
    }
}