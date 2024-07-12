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
    fun macroCallWrapper(modifier: Int?, elementType: IElementType?, frameName: String?) {
        if (frameName == null && modifier != _LEFT_) return
        if (tokenType == MACRO_OBJECT) {
            val marker = enter_section_without_macro(this)
            super.advanceLexer()
            exit_section_(this, marker, VARIABLE_IDENTIFIER, true)
        } else if (tokenType == MACRO_FUNCTION) {
            var marker = enter_section_without_macro(this)
            super.advanceLexer()
            exit_section_(this, marker, VARIABLE_IDENTIFIER, true)
            marker = enter_section_without_macro(this)
            while (true) {
                if (tokenType == null || tokenType == RIGHT_PAREN_MACRO_CALL) break
                super.advanceLexer()
            }
            super.advanceLexer()
            exit_section_(this, marker, DUMMY_MACRO_BLOCK, true)
        }
    }
}