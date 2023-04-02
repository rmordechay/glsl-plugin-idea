package glsl.plugin.language

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.lang.parser.GeneratedParserUtilBase
import com.intellij.psi.tree.IElementType
import glsl.GlslTypes.PP_END
import glsl._GlslParser

class GlslParserAdapter : _GlslParser() {
    override fun parse(root: IElementType, builder: PsiBuilder): ASTNode {
        return super.parse(root, builder)
    }
}

object GlslParserUtils : GeneratedParserUtilBase() {

    @JvmStatic
    fun ppDefineToken(builder: PsiBuilder, level: Int): Boolean {
        val tokenType = builder.tokenType
        return tokenType != PP_END && consumeToken(builder, tokenType)
    }
}