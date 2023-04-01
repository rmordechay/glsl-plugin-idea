package glsl.plugin.language

import com.intellij.lexer.LexerBase
import com.intellij.psi.TokenType.WHITE_SPACE
import com.intellij.psi.tree.IElementType
import glsl.GlslTypes.*
import glsl._GlslLexer
import glsl.plugin.psi.GlslInclude.Companion.isValidIncludePath
import glsl.plugin.utils.GlslUtils
import glsl.psi.interfaces.GlslExternalDeclaration

class GlslLexerAdapter : LexerBase() {
    private val lexer = _GlslLexer(null)
    private var state = 0
    private var tokenType: IElementType? = null
    private var bufferSequence: CharSequence = ""
    private var tokenText = ""
    private var tokenStart = 0
    private var tokenEnd = 0
    private var prevStart = 0
    private var prevEnd = 0
    private var bufferEnd = 0
    private var macroExpansion: MacroExpansion? = null
    private val macrosTokens = hashMapOf<String, ArrayList<IElementType>>()

    /**
     *
     */
    override fun getTokenType(): IElementType? {
        if (macroExpansion != null) {
            expandMacro()
        } else if (tokenType == PP_INCLUDE) {
            resolveInclude()
        } else if (tokenType == PP_DEFINE) {
            setDefineDefinition()
        } else if (isMacro()) {
//            setMacroExpansion()
//            tokenType = MACRO_EXPANSION
        }
        return tokenType
    }

    /**
     *
     */
    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        tokenType = null
        bufferSequence = buffer
        tokenStart = startOffset
        tokenEnd = startOffset
        bufferEnd = endOffset
        lexer.reset(bufferSequence, startOffset, endOffset, initialState)
        advance()
    }

    /**
     *
     */
    override fun advance() {
        if (macroExpansion != null) return
        prevStart = tokenStart
        prevEnd = tokenEnd
        tokenType = lexer.advance()
        tokenStart = lexer.tokenStart
        tokenEnd = lexer.tokenEnd
        tokenText = lexer.yytext().toString()
        state = lexer.yystate()
    }

    /**
     *
     */
    override fun getState(): Int {
        return state
    }

    /**
     *
     */
    override fun getBufferSequence(): CharSequence {
        return bufferSequence
    }

    /**
     *
     */
    override fun getTokenStart(): Int {
        return if (macroExpansion != null && macroExpansion!!.expansionStarted()) {
            macroExpansion!!.endOffset
        } else {
            tokenStart
        }
    }

    /**
     *
     */
    override fun getTokenEnd(): Int {
        return if (macroExpansion != null) {
            macroExpansion!!.endOffset
        } else {
            tokenEnd
        }
    }

    /**
     *
     */
    override fun getBufferEnd(): Int {
        return bufferEnd
    }

    /**
     *
     */
    private fun resolveInclude() {
        var includePath = peek()
        if (!isValidIncludePath(includePath)) return
        includePath = includePath.substring(1, includePath.length - 1)
        val psiFile = GlslUtils.getPsiFileByPath(includePath)
        val children = psiFile?.children ?: return
        for (child in children) {
            if (child !is GlslExternalDeclaration) continue
            val typeSpecifier = child.declaration?.singleDeclaration?.getAssociatedType()?.getTypeText() ?: continue
            lexer.userTypesTable?.add(typeSpecifier)
        }
    }

    /**
     *
     */
    private fun setDefineDefinition() {
        val text = peek()
        val identifier = text.substringBefore(" ")
        if (macrosTokens.containsKey(identifier)) return
        if (text.indexOf(" ") == -1) { // #define can also have empty value
            macrosTokens[identifier] = arrayListOf(WHITE_SPACE)
            return
        }
        val body = text.substringAfter(" ")
        val bodyLexer = GlslLexerAdapter()
        bodyLexer.start(body)
        while (true) {
            val bodyTokenType = bodyLexer.tokenType ?: break
            if (bodyTokenType != WHITE_SPACE) {
                macrosTokens.getOrPut(identifier) { arrayListOf() }.add(bodyTokenType)
            }
            bodyLexer.advance()
        }
    }

    /**
     *
     */
    private fun expandMacro() {
        val nextToken = macroExpansion?.getNextToken()
        if (nextToken == null) {
            macroExpansion = null
            advance()
        } else {
            tokenType = nextToken
        }
    }

    /**
     *
     */
    private fun setMacroExpansion() {
        val tokens = macrosTokens[tokenText] ?: return
        macroExpansion = MacroExpansion(tokens, tokenEnd, tokenEnd)
    }

    /**
     *
     */
    private fun peek(): String {
        val currentText = tokenText
        val currentState = state
        val currentTokenType = tokenType
        val currentTokenStart = tokenStart
        val currentTokenEnd = tokenEnd
        advance()
        if (tokenType == WHITE_SPACE) advance()
        val peekText = tokenText.trim()
        tokenText = currentText
        state = currentState
        tokenType = currentTokenType
        tokenStart = currentTokenStart
        tokenEnd = currentTokenEnd
        lexer.reset(bufferSequence, tokenEnd, bufferEnd, state)
        return peekText
    }

    /**
     *
     */
    private fun isMacro(): Boolean {
        return (tokenType == IDENTIFIER || tokenType == TYPE_NAME_IDENTIFIER) && !lexer.afterType && !lexer.afterTypeQualifier && macrosTokens.containsKey(
            tokenText
        )
    }

    inner class MacroExpansion(private val tokens: List<IElementType>, val startOffset: Int, val endOffset: Int, private var tokenIndex: Int = 0) {
        fun getNextToken(): IElementType? {
            return if (tokenIndex >= tokens.size) null else tokens[tokenIndex++]
        }

        fun expansionStarted(): Boolean {
            return tokenIndex != 0
        }
    }
}
