package glsl.plugin.language

import com.intellij.lexer.LexerBase
import com.intellij.psi.TokenType.WHITE_SPACE
import com.intellij.psi.tree.IElementType
import glsl.GlslTypes.*
import glsl._GlslLexer
import glsl._GlslLexer.PREPROCESSOR_DEFINE
import glsl.plugin.language.GlslLanguage.Companion.MACRO_CALL

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

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        lexer.reset(buffer, startOffset, endOffset, initialState)
        myText = buffer
        myEndOffset = endOffset
        myTokenType = lexer.advance()
    }

    override fun getState(): Int {
        return lexer.yystate()
    }

    override fun getTokenType(): IElementType? {
        if (expansionTokens != null && expansionTokens?.hasNext() == true) {
            myTokenType = expansionTokens?.next()
            if (!expansionTokens!!.hasNext()) expansionTokens = null
        } else if (state != PREPROCESSOR_DEFINE && myTokenType == IDENTIFIER && lexer.yytext() in macrosDefines) {
            expansionTokens = macrosDefines[lexer.yytext()]?.iterator()
            myTokenType = MACRO_CALL
        }
        return myTokenType
    }

    override fun getTokenStart(): Int {
        return lexer.tokenStart
    }

    override fun getTokenEnd(): Int {
        if (expansionTokens != null) return lexer.tokenStart
        return lexer.tokenEnd
    }

    override fun advance() {
        if (expansionTokens != null) return
        if (myTokenType == PP_DEFINE) cacheDefineDefinition()
        myTokenType = lexer.advance()
    }

    override fun getBufferSequence(): CharSequence {
        return myText
    }

    override fun getBufferEnd(): Int {
        return myEndOffset
    }

    /**
     *
     */
    private fun cacheDefineDefinition() {
        val lastEnd = tokenEnd
        val lastState = state
        lexer.advance() // White space
        lexer.advance()
        val defineIdentifier = lexer.yytext().toString()
        lexer.advance()
        val defineBodyString = lexer.yytext().toString().trim()
        addDefineMacro(defineIdentifier, defineBodyString)
        lexer.reset(bufferSequence, lastEnd, myEndOffset, lastState)
    }

    /**
     *
     */
    private fun addDefineMacro(identifier: String, defineBodyString: String) {
        val helperLexer = _GlslLexer(null)
        val elements = arrayListOf<IElementType>()
        helperLexer.reset(defineBodyString, 0, defineBodyString.length, 0)
        while (true) {
            val nextToken = helperLexer.advance() ?: break
            if (nextToken == WHITE_SPACE) continue
            elements.add(nextToken)
        }
        macrosDefines[identifier] = elements
    }
}