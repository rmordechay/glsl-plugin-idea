package glsl.plugin.language

import com.intellij.lexer.LexerBase
import com.intellij.psi.TokenType.WHITE_SPACE
import com.intellij.psi.tree.IElementType
import glsl.GlslTypes.*
import glsl._GlslLexer
import glsl._GlslLexer.*


class GlslMacro(val name: String, val macroDefineType: IElementType) {
    val elements = arrayListOf<Pair<String, IElementType>>()
    var expansionIter: Iterator<Pair<String, IElementType>>? = null
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
    private var myTokenText: String? = null
    private var macros = hashMapOf<String, GlslMacro>()
    private var macroDefine: GlslMacro? = null
    private var macroExpansion: GlslMacro? = null

    /**
     *
     */
    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        lexer.reset(buffer, startOffset, endOffset, initialState)
        myText = buffer.toString()
        myBufferEnd = endOffset
        advance()
    }

    /**
     *
     */
    override fun advance() {
        if (macroExpansion != null) return
        myTokenType = lexer.advance()
        myTokenText = lexer.yytext().toString()
    }

    /**
     *
     */
    override fun getTokenType(): IElementType? {
        if (isMacroCall()) {
            macroExpansion = macros[tokenText]!!
            macroExpansion!!.expansionIter = macroExpansion?.elements?.iterator()
        }
        if (macroExpansion != null) {
            if (macroExpansion!!.expansionIter?.hasNext() == true) {
                val nextMacroToken = macroExpansion!!.expansionIter?.next()
                myTokenText = nextMacroToken?.first
                myTokenType = nextMacroToken?.second
            } else {
                myTokenText = macroExpansion!!.name
                myTokenType = macroExpansion!!.macroDefineType
                macroExpansion = null
            }
        }
        if (state == MACRO_IDENTIFIER_STATE && myTokenType == IDENTIFIER) {
            macroDefine = GlslMacro(tokenText, getMacroType())
        } else if (state == MACRO_BODY_STATE) {
            if (myTokenType == PP_END) {
                lexer.yybegin(YYINITIAL)
                macros[macroDefine!!.name] = macroDefine!!
                macroDefine = null
            } else if (myTokenType !in listOf(WHITE_SPACE, RIGHT_PAREN_MACRO, LINE_COMMENT, MULTILINE_COMMENT)) {
                macroDefine!!.elements.add(Pair(myTokenText!!, myTokenType!!))
            }
        }
        return myTokenType
    }

    /**
     *
     */
    override fun getTokenText(): String {
        return lexer.yytext().toString()
    }

    /**
     *
     */
    override fun getState(): Int {
        return lexer.yystate()
    }

    /**
     *
     */
    override fun getTokenStart(): Int {
        return lexer.tokenStart
    }

    /**
     *
     */
    override fun getTokenEnd(): Int {
        return lexer.tokenEnd
    }

    /**
     *
     */
    override fun getBufferSequence(): CharSequence {
        return myText
    }

    /**
     *
     */
    override fun getBufferEnd(): Int {
        return myBufferEnd
    }

    /**
     *
     */
    private fun isMacroCall(): Boolean {
        return myTokenType == IDENTIFIER && myTokenText in macros
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
