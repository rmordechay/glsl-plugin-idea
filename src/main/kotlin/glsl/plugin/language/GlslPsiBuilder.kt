package glsl.plugin.language

import com.intellij.lang.PsiBuilder
import glsl.GlslTypes
import glsl.GlslTypes.MACRO_CALL_FUNCTION
import glsl.GlslTypes.MACRO_CALL_OBJECT
import glsl._GlslParser
import utils.GeneratedParserUtil
import utils.GeneratedParserUtil.nextTokenIs

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
        if (nextTokenIs(this, MACRO_CALL_OBJECT) || nextTokenIs(this, MACRO_CALL_FUNCTION)) {
            val marker = GeneratedParserUtil.enter_section_(this)
            super.advanceLexer()
            GeneratedParserUtil.exit_section_(this, marker, GlslTypes.VARIABLE_IDENTIFIER, true)
            return true
        }
        return false
    }
}