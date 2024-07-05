package glsl.plugin.language

import com.intellij.lang.PsiBuilder
import glsl.GlslTypes.*
import glsl._GlslParser
import glsl._GlslParser.*
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
            return variable_identifier(this, 1)
        } else if (tokenType == MACRO_FUNCTION) {
            val marker = GeneratedParserUtil.enter_section_(this)
            super.advanceLexer()
            GeneratedParserUtil.exit_section_(this, marker, VARIABLE_IDENTIFIER, true)

            var nestingLevel = 1
            super.advanceLexer() // Opening paren
            while (nestingLevel > 0) {
                val innerMarker = GeneratedParserUtil.enter_section_(this)
                super.advanceLexer()
                if (tokenType == IDENTIFIER) {
                    GeneratedParserUtil.exit_section_(this, innerMarker, VARIABLE_IDENTIFIER, true)
                } else {
                    GeneratedParserUtil.exit_section_(this, innerMarker, EXPR, true)
                }
                if (tokenType == LEFT_PAREN) nestingLevel++
                else if (tokenType == RIGHT_PAREN) nestingLevel--
                else if (tokenType == null) break
            }
            super.advanceLexer()  // Closing paren
            return true
        }
        return false
    }
}