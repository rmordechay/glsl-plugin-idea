package glsl.plugin.language

import com.intellij.lexer.LexerBase
import com.intellij.psi.TokenType.WHITE_SPACE
import com.intellij.psi.tree.IElementType
import glsl.GlslTypes
import glsl.GlslTypes.PP_DEFINE
import glsl.GlslTypes.PP_INCLUDE
import glsl._GlslLexer
import glsl.plugin.utils.GlslUtils
import glsl.plugin.utils.GlslUtils.isValidIncludePath
import glsl.psi.interfaces.GlslExternalDeclaration

class GlslLexerAdapter : LexerBase() {
    private val lexer = _GlslLexer(null)
    private var state = 0
    private var tokenType: IElementType? = null
    private var bufferSequence: CharSequence = ""
    private var tokenText = ""
    private var tokenStart = 0
    private var tokenEnd = 0
    private var bufferEnd = 0
    private var macroExpansion: MacroExpansion? = null
    private val macrosTokens = hashMapOf<String, MutableList<IElementType>>()

    override fun getTokenType(): IElementType? {
        if (macroExpansion != null) {
            expandMacro()
        } else  if (tokenType == PP_INCLUDE) {
            resolveInclude()
        } else if (tokenType == PP_DEFINE) {
            setDefineDefinition()
        } else if (isMacro()) {
            setMacroExpansion()
        }
        return tokenType
    }

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        tokenType = null
        bufferSequence = buffer
        tokenStart = startOffset
        tokenEnd = startOffset
        bufferEnd = endOffset
        lexer.reset(bufferSequence, startOffset, endOffset, initialState)
        advance()
    }

    override fun advance() {
        if (macroExpansion != null) return
        tokenType = lexer.advance()
        tokenStart = lexer.tokenStart
        tokenEnd = lexer.tokenEnd
        tokenText = lexer.yytext().toString()
        state = lexer.yystate()
    }

    override fun getState(): Int {
        return state
    }

    override fun getBufferSequence(): CharSequence {
        return bufferSequence
    }

    override fun getTokenStart(): Int {
        return if (macroExpansion != null && macroExpansion!!.tokenIndex > 0) {
            macroExpansion!!.endOffset
        } else {
            tokenStart
        }
    }

    override fun getTokenEnd(): Int {
        return if (macroExpansion != null) {
            macroExpansion!!.endOffset
        } else {
            tokenEnd
        }
    }

    override fun getBufferEnd(): Int {
        return bufferEnd
    }

    /**
     *
     */
    private fun resolveInclude() {
        var includePath = lookAhead()
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
        val text = lookAhead().trim()
        val identifier = text.substringBefore(" ")
        val body = text.substringAfter(" ")
        val bodyLexer = GlslLexerAdapter()
        bodyLexer.start(body)
        while (true) {
            val bodyTokenType = bodyLexer.tokenType ?: break
            if (bodyTokenType != WHITE_SPACE) {
                macrosTokens.getOrPut(identifier) { mutableListOf() }.add(bodyTokenType)
            }
            bodyLexer.advance()
        }
    }

    /**
     *
     */
    private fun expandMacro() {
        val nextToken = macroExpansion!!.getNextToken()
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
        macroExpansion = MacroExpansion(tokens, tokenStart, tokenEnd)
        tokenType = WHITE_SPACE
    }

    /**
     *
     */
    private fun lookAhead() : String {
        val currentText = tokenText
        val currentState = state
        val currentTokenType = tokenType
        val currentTokenStart = tokenStart
        val currentTokenEnd = tokenEnd
        advance()
        val lookAheadText = tokenText
        tokenText = currentText
        state = currentState
        tokenType = currentTokenType
        tokenStart = currentTokenStart
        tokenEnd = currentTokenEnd
        lexer.reset(bufferSequence, tokenEnd, bufferEnd, state)
        return lookAheadText
    }

    /**
     *
     */
    private fun isMacro() : Boolean {
        return tokenType == GlslTypes.IDENTIFIER  && macrosTokens.containsKey(tokenText)
    }
}

class MacroExpansion(private val tokens: List<IElementType>, val startOffset: Int, val endOffset: Int, var tokenIndex: Int = 0) {
    fun getNextToken(): IElementType? {
        return if (tokenIndex >= tokens.size) null else tokens[tokenIndex++]
    }
}