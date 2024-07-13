package glsl.plugin.psi

import com.intellij.lang.PsiBuilder
import com.intellij.psi.tree.IElementType
import glsl.GlslTypes.*
import glsl._GlslParser
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
        if (myTokenType == MACRO_OBJECT) {
            val marker = enter_section_(this)
            super.advanceLexer()
            exit_section_(this, marker, VARIABLE_IDENTIFIER, true)
        } else if (myTokenType == MACRO_FUNCTION) {
            val marker = enter_section_(this)
            super.advanceLexer()
            myTokenType = super.getTokenType()
            exit_section_(this, marker, VARIABLE_IDENTIFIER, true)
            while (true) {
                if (myTokenType == null || myTokenType == RIGHT_PAREN_MACRO_CALL) break
                if (myTokenType == IDENTIFIER) {
                    val identifierMarker = enter_section_(this)
                    super.advanceLexer()
                    exit_section_(this, identifierMarker, VARIABLE_IDENTIFIER, true)
                } else {
                    super.advanceLexer()
                }
                myTokenType = super.getTokenType()
            }
            super.advanceLexer()
        }
    }
}