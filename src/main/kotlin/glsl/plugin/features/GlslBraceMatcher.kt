package glsl.plugin.features

import com.intellij.codeInsight.highlighting.PairedBraceMatcherAdapter
import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import glsl.GlslTypes
import glsl.plugin.language.GlslLanguage

/**
 *
 */
class GlslBraceMatcher : PairedBraceMatcherAdapter(GlslBraceMatcherBase(), GlslLanguage.INSTANCE)

/**
 *
 */
private class GlslBraceMatcherBase : PairedBraceMatcher {
    /**
    *
    */
    override fun getPairs(): Array<BracePair> {
        return arrayOf(
            BracePair(GlslTypes.LEFT_BRACE, GlslTypes.RIGHT_BRACE, true),
            BracePair(GlslTypes.LEFT_PAREN, GlslTypes.RIGHT_PAREN, false),
            BracePair(GlslTypes.LEFT_BRACKET, GlslTypes.RIGHT_BRACKET, false),
        )
    }

    /**
    *
    */
    override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?): Boolean {
        return contextType in TokenSet.orSet(
            TokenSet.create(GlslTypes.LINE_COMMENT),
            TokenSet.create(
                TokenType.WHITE_SPACE,
                GlslTypes.SEMICOLON,
                GlslTypes.COMMA,
                GlslTypes.RIGHT_PAREN,
                GlslTypes.RIGHT_BRACKET,
                GlslTypes.RIGHT_BRACE,
                GlslTypes.LEFT_BRACE
            )
        )
    }

    /**
    *
    */
    override fun getCodeConstructStart(file: PsiFile?, openingBraceOffset: Int): Int {
        return openingBraceOffset
    }
}