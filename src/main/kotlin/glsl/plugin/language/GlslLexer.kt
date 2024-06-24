package glsl.plugin.language

import com.intellij.lexer.LexerBase
import com.intellij.psi.TokenType.WHITE_SPACE
import com.intellij.psi.tree.IElementType
import com.jetbrains.rd.util.string.println
import glsl.GlslTypes
import glsl.GlslTypes.PP_DEFINE
import glsl._GlslLexer
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
    private val funcRegex = "(\\w+)\\([^)]*\\)\\s+(.*)".toRegex()
    private val objectRegex = "(^\\w+)\\s+(.*)".toRegex()
    private var inExpansion: Iterator<IElementType>? = null

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
        if (inExpansion != null && inExpansion!!.hasNext()) {
            myTokenType = inExpansion?.next()
            if (!inExpansion!!.hasNext()) inExpansion = null
        } else if (myTokenType == GlslTypes.IDENTIFIER && lexer.yytext() in macrosDefines) {
            inExpansion = macrosDefines[lexer.yytext()]?.iterator()
            myTokenType = MACRO_CALL
        }
        return myTokenType
    }

    override fun getTokenStart(): Int {
        return lexer.tokenStart
    }

    override fun getTokenEnd(): Int {
        if (inExpansion != null) return lexer.tokenStart
        return lexer.tokenEnd
    }

    override fun advance() {
        if (inExpansion != null) return
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
        lexer.advance()

        val defineString = lexer.yytext().toString().trim()
        if (funcRegex.matches(defineString)) {
            addDefineMacro(defineString, funcRegex)
        } else if (objectRegex.matches(defineString)) {
            addDefineMacro(defineString, objectRegex)
        }
        lexer.reset(bufferSequence, lastEnd, myEndOffset, lastState)
    }

    /**
     *
     */
    private fun addDefineMacro(defineString: String, regex: Regex) {
        val identifier = regex.find(defineString)?.groups?.get(1)?.value
        val body = regex.find(defineString)?.groups?.get(2)?.value
        if (identifier != null && body != null) {
            val helperLexer = _GlslLexer(null)
            val elements = arrayListOf<IElementType>()
            helperLexer.reset(body, 0, body.length, 0)
            while (true) {
                val nextToken = helperLexer.advance() ?: break
                if (nextToken == WHITE_SPACE) continue
                elements.add(nextToken)
            }
            macrosDefines[identifier] = elements
        }
    }
}