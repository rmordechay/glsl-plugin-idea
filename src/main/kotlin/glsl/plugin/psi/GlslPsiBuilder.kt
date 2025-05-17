package glsl.plugin.psi

import com.intellij.lang.PsiBuilder
import com.intellij.lang.parser.GeneratedParserUtilBase
import com.intellij.lang.parser.GeneratedParserUtilBase.enter_section_
import com.intellij.lang.parser.GeneratedParserUtilBase.exit_section_
import com.intellij.psi.tree.IElementType
import glsl.GlslTypes.*
import glsl._GlslParser
import glsl.data.GlslTokenSets.IDENTIFIERS

/**
 *
 */
class GlslPsiBuilder(builder: PsiBuilder, state: GeneratedParserUtilBase.ErrorState, parser: _GlslParser) : GeneratedParserUtilBase.Builder(builder, state, parser) {
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
            exit_section_(this, marker, VARIABLE_IDENTIFIER, true)

            myTokenType = super.getTokenType()
            if (myTokenType != LEFT_PAREN_MACRO_CALL) return

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