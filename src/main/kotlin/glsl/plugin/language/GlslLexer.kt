package glsl.plugin.language

import com.intellij.lexer.LexerBase
import com.intellij.psi.TokenType.WHITE_SPACE
import com.intellij.psi.tree.IElementType
import glsl.GlslTypes.*
import glsl._GlslLexer
import glsl._GlslLexer.*


class GlslMacro(val name: String, val macroDefineType: IElementType) {
    val elements = mutableListOf<IElementType?>()
}

/**
 *
 */
class GlslLexer : LexerBase() {
    private val lexer = _GlslLexer()
    private val helperLexer = _GlslLexer()
    private var myText: String = ""
    private var myBufferEnd: Int = 0
    private var myTokenType: IElementType? = null
    private var macros = hashMapOf<String, GlslMacro>()
    private var macrosCandidate: GlslMacro? = null

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        lexer.reset(buffer, startOffset, endOffset, initialState)
        myText = buffer.toString()
        myBufferEnd = endOffset
        myTokenType = lexer.advance()
    }

    override fun advance() {
        myTokenType = lexer.advance()
    }

    override fun getTokenType(): IElementType? {
        if (state == MACRO_IDENTIFIER_STATE && myTokenType == IDENTIFIER) {
            macrosCandidate = GlslMacro(tokenText, getMacroType())
        } else if (macrosCandidate != null) {
            if (myTokenType == PP_END) {
                macros[macrosCandidate!!.name] = macrosCandidate!!
                macrosCandidate = null
            } else if (myTokenType !in listOf(WHITE_SPACE, RIGHT_PAREN_MACRO)) {
                macrosCandidate!!.elements.add(myTokenType)
            }
        }
        return myTokenType
    }

    override fun getTokenText(): String {
        return lexer.yytext().toString()
    }

    override fun getState(): Int {
        return lexer.yystate()
    }

    override fun getTokenStart(): Int {
        return lexer.tokenStart
    }

    override fun getTokenEnd(): Int {
        return lexer.tokenEnd
    }

    override fun getBufferSequence(): CharSequence {
        return myText
    }

    override fun getBufferEnd(): Int {
        return myBufferEnd
    }

    /**
     *
     */
    private fun getMacroType(): IElementType {
        helperLexer.reset(myText, tokenEnd, bufferEnd, YYINITIAL)
        val nextToken = helperLexer.advance()
        if (nextToken == LEFT_PAREN) {
            lexer.yybegin(MACRO_FUNC_DEFINITION_STATE)
            return MACRO_FUNCTION
        }
        lexer.yybegin(MACRO_BODY_STATE)
        return MACRO_OBJECT
    }
}
