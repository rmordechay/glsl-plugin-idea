package glsl.plugin.language

import com.intellij.lang.PsiBuilder
import glsl.GlslTypes.*
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
        if (tokenType == MACRO_OBJECT) {
            val marker = GeneratedParserUtil.enter_section_(this)
            super.advanceLexer()
            GeneratedParserUtil.exit_section_(this, marker, VARIABLE_IDENTIFIER, true)
            return true
        } else if (tokenType == MACRO_FUNCTION) {
            var marker = GeneratedParserUtil.enter_section_(this)
            super.advanceLexer()
            GeneratedParserUtil.exit_section_(this, marker, VARIABLE_IDENTIFIER, true)

            var parenCount = 1
            super.advanceLexer() // Opening paren
            marker = GeneratedParserUtil.enter_section_(this)
            while (parenCount > 0) {
                super.advanceLexer()
                if (tokenType == LEFT_PAREN) parenCount++
                else if (tokenType == RIGHT_PAREN) parenCount--
                else if (tokenType == null) break
            }
            super.advanceLexer()
            GeneratedParserUtil.exit_section_(this, marker, EXPR, true)
            return true
        }
        return false
    }
}