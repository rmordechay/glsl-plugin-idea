package glsl.plugin.language

import com.intellij.lexer.LexerBase
import com.intellij.psi.TokenType.WHITE_SPACE
import com.intellij.psi.tree.IElementType
import com.intellij.util.containers.addIfNotNull
import glsl.GlslTypes.*
import glsl._GlslLexer
import glsl._GlslLexer.*


class MacroDefine(val macroDefineName: String, val macroDefineType: IElementType) {
    val elements = mutableListOf<IElementType>()
}

/**
 *
 */
class GlslLexer : LexerBase() {
    private var myText: CharSequence = ""
    private var myBufferEnd = 0
    private var myTokenType: IElementType? = null
    private val lexer = _GlslLexer(null)
    private val helperLexer = _GlslLexer(null)

    private var macroDefine: MacroDefine? = null
    private val macrosDefines = hashMapOf<String, MacroDefine>()
    private var macroExpansionTokens: Iterator<IElementType>? = null

    /**
     *
     */
    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        lexer.reset(buffer, startOffset, endOffset, initialState)
        myText = buffer
        myBufferEnd = endOffset
        myTokenType = lexer.advance()
    }

    /**
     *
     */
    override fun advance() {
        if (macroExpansionTokens != null) return
        if (state == MACRO_IDENTIFIER_STATE && myTokenType == IDENTIFIER) {
            setMacroDefine()
        } else if (state == PP_STATE && myTokenType !in listOf(RIGHT_PAREN_MACRO, WHITE_SPACE)) {
            macroDefine?.elements?.addIfNotNull(myTokenType)
        } else if (myTokenType == PP_END && macroDefine != null) {
            macrosDefines[macroDefine?.macroDefineName!!] = macroDefine!!
            macroDefine = null
        }
        myTokenType = lexer.advance()
    }

    /**
     *
     */
    override fun getTokenType(): IElementType? {
        if (state != MACRO_IDENTIFIER_STATE && myTokenType == IDENTIFIER && lexer.yytext() in macrosDefines) {
            macroExpansionTokens = macrosDefines[lexer.yytext()]?.elements?.iterator()
            myTokenType = macrosDefines[lexer.yytext()]?.macroDefineType
            if (macroExpansionTokens!!.hasNext()) {
                return macroExpansionTokens!!.next()
            }
        } else if (macroExpansionTokens != null) {
            if (macroExpansionTokens!!.hasNext()) {
                return macroExpansionTokens!!.next()
            } else {
                macroExpansionTokens = null
            }
        }
        return myTokenType
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
        if (macroExpansionTokens != null) {
            return lexer.tokenStart
        }
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
    private fun setMacroDefine() {
        val macroDefineType: IElementType
        if (lookAhead() == LEFT_PAREN) {
            macroDefineType = MACRO_FUNCTION
            lexer.yybegin(MACRO_FUNC_DEFINITION_STATE)
        } else {
            macroDefineType = MACRO_OBJECT
            lexer.yybegin(PP_STATE)
        }
        macroDefine = MacroDefine(lexer.yytext().toString(), macroDefineType)
    }

    /**
     *
     */
    private fun lookAhead(): IElementType? {
        helperLexer.reset(bufferSequence, tokenEnd, bufferEnd, YYINITIAL)
        return helperLexer.advance()
    }
}
