package glsl.plugin.language

import com.intellij.lexer.LexerBase
import com.intellij.psi.TokenType.WHITE_SPACE
import com.intellij.psi.tree.IElementType
import glsl.GlslTypes.*
import glsl._GlslLexer
import glsl._GlslLexer.*
import glsl.plugin.language.GlslLanguage.Companion.MACRO_CALL

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

    private var macroDefineId: String? = null
    private var macroDefineBody: String? = null
    private val macrosDefines = hashMapOf<String, List<IElementType>>()

    private var macroCallName: CharSequence? = ""
    private var inMacroFuncCall = false
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
            macroDefineId = lexer.yytext().toString()
            macroDefineBody = ""
            determineMacroType()
        } else if (state == MACRO_BODY_STATE) {
            macroDefineBody += lexer.yytext().toString()
        } else if (myTokenType == PP_END && macroDefineId != null) {
            macrosDefines[macroDefineId!!] = lexMacroBody()
            macroDefineId = null
            macroDefineBody = null
        }
        prevState = state
        if (state == MACRO_FUNC_DEFINITION_STATE && myTokenType == RIGHT_PAREN) {
            lexer.yybegin(MACRO_BODY_STATE)
        }
        myTokenType = lexer.advance()
    }

    /**
     *
     */
    override fun getTokenType(): IElementType? {
        if (macroExpansionTokens != null) {
            if (macroExpansionTokens!!.hasNext()) {
                val nextToken = macroExpansionTokens!!.next()
                if (state == DUMMY_STATE) {
                    lexer.yybegin(prevState)
                } else if (nextToken == myTokenType) {
                    lexer.yybegin(DUMMY_STATE)
                }
                myTokenType = nextToken
            } else {
                macroExpansionTokens = null
                macroCallName = null
                myTokenType = MACRO_CALL
            }
        } else if (shouldStartExpendingMacro()) {
            macroExpansionTokens = macrosDefines[macroCallName]?.iterator()
//            myTokenType = expansionTokens!!.next()
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
    private fun determineMacroType() {
        if (lookAhead() == LEFT_PAREN) {
            lexer.yybegin(MACRO_FUNC_DEFINITION_STATE)
        } else {
            lexer.yybegin(MACRO_BODY_STATE)
        }
    }


    /**
     *
     */
    private fun lexMacroBody(): List<IElementType> {
        val elements = arrayListOf<IElementType>()
        helperLexer.reset(macroDefineBody, 0, macroDefineBody?.length ?: 0, 0)
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
        while (true) {
            val nextToken = helperLexer.advance()
            if (nextToken == WHITE_SPACE) continue
            return nextToken
        }
    }

    /**
     *
     */
    private fun shouldStartExpendingMacro(): Boolean {
        val tokenText = lexer.yytext()
        val isMacroName = state != MACRO_IDENTIFIER_STATE && myTokenType == IDENTIFIER && tokenText in macrosDefines
        if (isMacroName) {
            macroCallName = tokenText
            inMacroFuncCall = lookAhead() == LEFT_PAREN
            return !inMacroFuncCall
        } else if (inMacroFuncCall) {
            if (myTokenType == RIGHT_PAREN) {
                inMacroFuncCall = false
                return true
            }
        }
        return false
    }
}
