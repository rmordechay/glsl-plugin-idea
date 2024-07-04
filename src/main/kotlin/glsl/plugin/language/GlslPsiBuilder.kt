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
        if (tokenType == MACRO_CALL_OBJECT) {
            val marker = GeneratedParserUtil.enter_section_(this)
            super.advanceLexer()
            GeneratedParserUtil.exit_section_(this, marker, VARIABLE_IDENTIFIER, true)
            return true
        } else if (tokenType == MACRO_CALL_FUNCTION) {
            var marker = GeneratedParserUtil.enter_section_(this)
            super.advanceLexer()
            GeneratedParserUtil.exit_section_(this, marker, VARIABLE_IDENTIFIER, true)

            super.advanceLexer()
            var parenCount = 1
            var shouldRun = true
            while (shouldRun) {
                if (parenCount == 0) shouldRun = false
                super.advanceLexer()
                marker = GeneratedParserUtil.enter_section_(this)
                if (tokenType == LEFT_PAREN) parenCount++
                if (tokenType == RIGHT_PAREN) parenCount--
                GeneratedParserUtil.exit_section_(this, marker, EXPR, true)
                if (tokenType == null) break
            }
            return true
        }
        return false
    }
}