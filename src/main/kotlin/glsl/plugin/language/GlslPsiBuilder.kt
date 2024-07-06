package glsl.plugin.language

import com.intellij.lang.PsiBuilder
import glsl.GlslTypes.*
import glsl._GlslParser
import glsl._GlslParser.variable_identifier
import utils.GeneratedParserUtil
import utils.GeneratedParserUtil.enter_section_
import utils.GeneratedParserUtil.exit_section_

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
            super.advanceLexer()
            return true
        } else if (tokenType == MACRO_FUNCTION) {
            val result = variable_identifier(this, 1)
            if (!result) return false

            val externalMarker = enter_section_(this)
            var nestingLevel = 1
            super.advanceLexer() // Opening paren
            while (nestingLevel > 0) {
                val innerMarker = enter_section_(this)
                val prevTokenType = tokenType
                super.advanceLexer()
                if (prevTokenType in listOf(IDENTIFIER, MACRO_OBJECT, MACRO_FUNCTION)) {
                    exit_section_(this, innerMarker, VARIABLE_IDENTIFIER, true)
                } else {
                    exit_section_(this, innerMarker, DUMMY_MACRO_BLOCK, true)
                }
                if (prevTokenType == LEFT_PAREN) nestingLevel++
                else if (prevTokenType == RIGHT_PAREN) nestingLevel--
                else if (prevTokenType == null) break
            }
            super.advanceLexer()  // Closing paren
            exit_section_(this, externalMarker, DUMMY_MACRO_BLOCK, true)
            return true
        }
        return false
    }
}