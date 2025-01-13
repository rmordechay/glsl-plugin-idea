package glsl.plugin.language

import com.intellij.lang.PsiBuilder
import com.intellij.lang.parser.GeneratedParserUtilBase.*
import com.intellij.psi.tree.IElementType
import glsl._GlslParser
import glsl.plugin.psi.GlslPsiBuilder

/**
 *
 */
class GlslParserAdapter : _GlslParser() {
    
    /**
     *
     */
    override fun parseLight(root: IElementType, originalBuilder: PsiBuilder) {
        val state = ErrorState()
        ErrorState.initState(state, originalBuilder, root, EXTENDS_SETS_)
        val builder = GlslPsiBuilder(originalBuilder, state, this)
        val marker = enter_section_(builder, 0, _COLLAPSE_, null)
        val result = parse_root_(root, builder)
        exit_section_(builder, 0, marker, root, result, true, TRUE_CONDITION)
    }
}