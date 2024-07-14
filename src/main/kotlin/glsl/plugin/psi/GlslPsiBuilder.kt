package glsl.plugin.psi

import com.intellij.lang.PsiBuilder
import com.intellij.psi.tree.IElementType
import glsl.GlslTypes.*
import glsl._GlslParser
import glsl.data.GlslTokenSets.IDENTIFIERS
import glsl.plugin.language.GlslLanguage.Companion.RIGHT_PAREN_MACRO_CALL
import utils.GeneratedParserUtil.*

/**
 *
 */
class GlslPsiBuilder(builder: PsiBuilder, state: ErrorState, parser: _GlslParser) : Builder(builder, state, parser) {
    /**
     *
     */
    override fun getTokenType(): IElementType? {
        macroCall()
        return super.getTokenType()
    }

    /**
     *
     */
    override fun getTokenText(): String? {
        macroCall()
        return super.getTokenText()
    }

    /**
     *
     */
    private fun macroCall() {
        var myTokenType = super.getTokenType()
        if (myTokenType == MACRO_OBJECT_CALL) {
            val marker = enter_section_(this)
            super.advanceLexer() // Opening parenthesis
            exit_section_(this, marker, VARIABLE_IDENTIFIER, true)
        } else if (myTokenType == MACRO_FUNCTION_CALL) {
            var marker = enter_section_(this)
            super.advanceLexer()
            myTokenType = super.getTokenType()
            exit_section_(this, marker, VARIABLE_IDENTIFIER, true)
            marker = enter_section_(this)
            while (true) {
                if (myTokenType == null || myTokenType == RIGHT_PAREN_MACRO_CALL) break
                if (myTokenType in IDENTIFIERS) {
                    val identifierMarker = enter_section_(this)
                    super.advanceLexer()
                    exit_section_(this, identifierMarker, VARIABLE_IDENTIFIER, true)
                } else {
                    super.advanceLexer()
                }
                myTokenType = super.getTokenType() // Closing parenthesis
            }
            super.advanceLexer()
            exit_section_(this, marker, MACRO_FUNC_CALL_PARAMS_BLOCK, true)
        }
    }
}