package glsl.plugin.language

import com.intellij.lang.PsiBuilder
import glsl.GlslTypes.*
import glsl._GlslParser
import utils.GeneratedParserUtil.*

/**
 *
 */
class GlslPsiBuilder(builder: PsiBuilder, state: ErrorState, parser: _GlslParser) : Builder(builder, state, parser) {

    /**
     *
     */
    fun macroCallWrapper() {
        if (tokenType == MACRO_OBJECT) {
            val marker = enter_section_(this)
            super.advanceLexer()
            exit_section_(this, marker, VARIABLE_IDENTIFIER, true)
        } else if (tokenType == MACRO_FUNCTION) {
            var marker = enter_section_(this)
            super.advanceLexer()
            exit_section_(this, marker, VARIABLE_IDENTIFIER, true)
            marker = enter_section_(this)
            var nestingLevel = 1
            super.advanceLexer() // Opening paren
            while (nestingLevel > 0) {
                super.advanceLexer()
                if (tokenType == LEFT_PAREN) nestingLevel++
                else if (tokenType == RIGHT_PAREN) nestingLevel--
                else if (tokenType == null) break
            }
            super.advanceLexer()
            exit_section_(this, marker, DUMMY_MACRO_BLOCK, true)
        }
    }
}