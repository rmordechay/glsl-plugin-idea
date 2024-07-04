package glsl.plugin.language

import com.intellij.lexer.LexerBase
import com.intellij.psi.TokenType.WHITE_SPACE
import com.intellij.psi.tree.IElementType
import glsl.GlslTypes.*
import glsl._GlslLexer
import glsl._GlslLexer.*


class MacroDefine(val macroDefineName: String, val macroDefineType: IElementType) {
    val elements = mutableListOf<IElementType>()
    var macroDefineBody: String = ""
}

/**
 *
 */
class GlslLexer : LexerBase() {
    private var myText: CharSequence = ""
    private var myBufferEnd = 0
    private var myTokenType: IElementType? = null
    private var prevState: Int = 0
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
        if (myTokenType == MACRO_IDENTIFIER) {
            setMacroDefine()
        } else if (myTokenType == PP_END && macroDefine != null) {
            macroDefine?.elements?.clear()
            macroDefine?.elements?.addAll(lexMacroBody())
            macrosDefines[macroDefine?.macroDefineName!!] = macroDefine!!
            macroDefine = null
        } else if (state == MACRO_BODY_STATE && myTokenType != RIGHT_PAREN_MACRO) {
            macroDefine?.macroDefineBody += lexer.yytext()
        }
        prevState = state
        myTokenType = lexer.advance()
    }

    /**
     *
     */
    override fun getTokenType(): IElementType? {
        if (myTokenType == IDENTIFIER && lexer.yytext() in macrosDefines) {
            macroExpansionTokens = macrosDefines[lexer.yytext()]?.elements?.iterator()
            myTokenType = macrosDefines[lexer.yytext()]?.macroDefineType
            if (macroExpansionTokens!!.hasNext()) return macroExpansionTokens!!.next()
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
            macroDefineType = MACRO_CALL_FUNCTION
            lexer.yybegin(MACRO_FUNC_DEFINITION_STATE)
        } else {
            macroDefineType = MACRO_CALL_OBJECT
            lexer.yybegin(MACRO_BODY_STATE)
        }
        macroDefine = MacroDefine(lexer.yytext().toString(), macroDefineType)
    }

    /**
     *
     */
    private fun lexMacroBody(): List<IElementType> {
        val elements = arrayListOf<IElementType>()
        helperLexer.reset(macroDefine?.macroDefineBody, 0, macroDefine?.macroDefineBody?.length ?: 0, 0)
        while (true) {
            val nextToken = helperLexer.advance() ?: break
            if (nextToken == WHITE_SPACE) continue
            elements.add(nextToken)
        }
        return elements
    }


    /**
     *
     */
    private fun lookAhead(): IElementType? {
        helperLexer.reset(bufferSequence, tokenEnd, bufferEnd, YYINITIAL)
        return helperLexer.advance()
    }
}
