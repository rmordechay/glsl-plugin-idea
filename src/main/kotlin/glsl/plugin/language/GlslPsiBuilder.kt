package glsl.plugin.language

import com.intellij.lang.PsiBuilder
import glsl.GlslTypes
import glsl._GlslParser
import utils.GeneratedParserUtil

/**
 *
 */
class GlslPsiBuilder(builder: PsiBuilder, state: GeneratedParserUtil.ErrorState, parser: _GlslParser) : GeneratedParserUtil.Builder(builder, state, parser) {

    /**
     *
     */
    override fun advanceLexer() {
        super.advanceLexer()
        macroCallWrapper()
    }

    /**
     *
     */
    private fun macroCallWrapper(): Boolean {
        if (!GeneratedParserUtil.nextTokenIsFast(this, GlslTypes.MACRO_CALL)) return false
        val marker = GeneratedParserUtil.enter_section_(this)
        super.advanceLexer()
        GeneratedParserUtil.exit_section_(this, marker, GlslTypes.VARIABLE_IDENTIFIER, true)
        return true
    }
}