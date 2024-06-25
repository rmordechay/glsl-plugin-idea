package glsl.plugin.language

import com.intellij.lexer.LexerBase
import com.intellij.psi.TokenType.WHITE_SPACE
import com.intellij.psi.tree.IElementType
import glsl.GlslTypes.*
import glsl._GlslLexer
import glsl._GlslLexer.MACRO_BODY_STATE


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
    private val helperLexer = _GlslLexer(null)
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
        if (shouldExpandMacro()) {
            val expansionToken = expansionTokens?.next()
            if (!expansionTokens!!.hasNext()) expansionTokens = null
            return expansionToken
        } else if (isMacroCall()) {
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
        helperLexer.reset(bufferSequence, 0, bufferEnd, 0)
        // Get macro name and body
        helperLexer.advance() // White space
        helperLexer.advance()
        val defineIdentifier = helperLexer.yytext().toString()
        var defineBodyString = ""
        // Collects all rows of define. More than one row is possible with backslash
        while (true) {
            val nextToken = helperLexer.advance()
            if (nextToken == null || nextToken == PP_END) break
            if (helperLexer.yytext() != "\\") {
                defineBodyString += helperLexer.yytext().toString().trim() + " "
            }
        }
        // Lex the body
        val elements = arrayListOf<IElementType>()
        helperLexer.reset(defineBodyString, 0, defineBodyString.length, 0)
        while (true) {
            val nextToken = helperLexer.advance() ?: break
            if (nextToken == WHITE_SPACE) continue
            elements.add(nextToken)
        }
        // Save the final results
        macrosDefines[defineIdentifier] = elements
    }

    /**
     *
     */
    private fun shouldExpandMacro(): Boolean {
        return expansionTokens != null && expansionTokens?.hasNext() == true
    }

    /**
     *
     */
    private fun isMacroCall(): Boolean {
        return state != MACRO_BODY_STATE && myTokenType == IDENTIFIER && lexer.yytext() in macrosDefines
    }
}
