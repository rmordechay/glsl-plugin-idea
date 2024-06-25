package glsl.plugin.language

import com.intellij.lexer.LexerBase
import com.intellij.openapi.util.RecursionManager
import com.intellij.openapi.util.RecursionManager.*
import com.intellij.psi.TokenType.WHITE_SPACE
import com.intellij.psi.tree.IElementType
import glsl.GlslTypes.*
import glsl._GlslLexer
import glsl._GlslLexer.PREPROCESSOR_DEFINE
import glsl.plugin.language.GlslLanguage.Companion.MACRO_CALL

const val RECURSION_LIMIT = 100_000

/**
 *
 */
class GlslLexer : LexerBase() {
    private var myText: CharSequence = ""
    private var myEndOffset = 0
    private var myTokenType: IElementType? = null
    private val lexer = _GlslLexer(null)
    private val macrosDefines = hashMapOf<String, List<IElementType>>()
    private var expansionTokens: Iterator<IElementType>? = null
    private var recursionGuard = 0


    /**
     *
     */
    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        lexer.reset(buffer, startOffset, endOffset, initialState)
        myText = buffer
        myEndOffset = endOffset
        myTokenType = lexer.advance()
        recursionGuard = 0
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
    override fun getTokenType(): IElementType? {
        if (expansionTokens != null && expansionTokens?.hasNext() == true) {
            val expansionToken = expansionTokens?.next()
            if (!expansionTokens!!.hasNext()) expansionTokens = null
            return expansionToken
        } else if (state != PREPROCESSOR_DEFINE && myTokenType == IDENTIFIER && lexer.yytext() in macrosDefines) {
            expansionTokens = macrosDefines[lexer.yytext()]?.iterator()
            myTokenType = MACRO_CALL
        }
        return myTokenType
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
        if (expansionTokens == null || recursionGuard++ >= RECURSION_LIMIT) {
            return lexer.tokenEnd
        }
        return lexer.tokenStart
    }

    /**
     *
     */
    override fun advance() {
        if (expansionTokens != null) return
        if (myTokenType == PP_DEFINE) setDefineMacro()
        myTokenType = lexer.advance()
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
        return myEndOffset
    }

    /**
     *
     */
    private fun setDefineMacro() {
        val helperLexer = _GlslLexer(null)
        val elements = arrayListOf<IElementType>()
        // Get macro name and body
        val lastEnd = tokenEnd
        val lastState = state
        lexer.advance() // White space
        lexer.advance()
        val defineIdentifier = lexer.yytext().toString()
        lexer.advance()
        val defineBodyString = lexer.yytext().toString().trim()
        helperLexer.reset(defineBodyString, 0, defineBodyString.length, 0)
        // Lex the body
        while (true) {
            val nextToken = helperLexer.advance() ?: break
            if (nextToken == WHITE_SPACE) continue
            elements.add(nextToken)
        }
        macrosDefines[defineIdentifier] = elements

        lexer.reset(bufferSequence, lastEnd, myEndOffset, lastState)
    }
}