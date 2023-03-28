package glsl.plugin.language

import com.intellij.lexer.FlexAdapter
import glsl._GlslLexer

class GlslLexerAdapter : FlexAdapter(_GlslLexer(null)) {

}