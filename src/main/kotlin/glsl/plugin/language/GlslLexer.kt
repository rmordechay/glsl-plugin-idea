package glsl.plugin.language

import com.intellij.lexer.LexerBase
import com.intellij.psi.TokenType.WHITE_SPACE
import com.intellij.psi.tree.IElementType
import com.intellij.util.containers.addIfNotNull
import glsl.GlslTypes.*
import glsl._GlslLexer
import glsl._GlslLexer.*

/**
 *
 */
class GlslMacro(val name: String, val macroDefineType: IElementType) {
    val elements = arrayListOf<Pair<String?, IElementType?>>()
    var params = mutableListOf<String>()
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

    private var inMacroFuncCall: Boolean = false
    private var macroFuncCallParams: HashMap<String, MutableList<IElementType>>? = null
    private var paramExpansionIter: Iterator<IElementType>? = null
    private var macroExpansionIter: Iterator<Pair<String?, IElementType?>>? = null
    private var macroFuncParamIndex: Int = 0
    private var macroParamNestingLevel: Int = 0
    private var macroFunc: GlslMacro? = null

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
        if (isMacroCallStart()) {
            setMacroCallData()
        } else if (paramExpansionIter != null) {
            expandMacroParam()
            if (paramExpansionIter == null) expandMacro()
        } else if (macroExpansion != null) {
            expandMacro()
        } else if (inMacroFuncCall) {
            addMacroParamToken()
        }
        if (state == MACRO_IDENTIFIER_STATE && myTokenType == IDENTIFIER) {
            macroDefine = GlslMacro(tokenText, getMacroType())
        } else if (state == MACRO_FUNC_DEFINITION_STATE && myTokenType == MACRO_FUNC_PARAM) {
            macroDefine!!.params.addIfNotNull(myTokenText)
        } else if (state == MACRO_BODY_STATE) {
            addMacroBodyToken()
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
    private fun setMacroCallData() {
        val macro = macros[tokenText] ?: return
        if (macro.macroDefineType == MACRO_FUNCTION) {
            inMacroFuncCall = true
            macroParamNestingLevel = 0
            macroFuncParamIndex = 0
            macroFuncCallParams = hashMapOf()
            macroFunc = macro
            myTokenType = MACRO_FUNCTION
        } else {
            macroExpansion = macro
            macroExpansionIter = macroExpansion?.elements?.iterator()
            myTokenType = MACRO_OBJECT
        }
    }

    /**
     *
     */
    private fun expandMacro() {
        if (macroExpansionIter?.hasNext() == true) {
            val nextMacroToken = macroExpansionIter?.next()
            myTokenText = nextMacroToken?.first
            myTokenType = nextMacroToken?.second
            if (myTokenText in macroExpansion!!.params) {
                paramExpansionIter = macroFuncCallParams?.get(myTokenText!!)?.iterator()
                myTokenType = paramExpansionIter?.next()
            }
        } else {
            macroExpansion = null
            advance()
        }
    }

    /**
     *
     */
    private fun expandMacroParam() {
        if (paramExpansionIter?.hasNext() == true) {
            myTokenType = paramExpansionIter?.next()
        } else {
            paramExpansionIter = null
        }
    }

    /**
     *
     */
    private fun addMacroBodyToken() {
        if (myTokenType == PP_END) {
            lexer.yybegin(YYINITIAL);
            macros[macroDefine!!.name] = macroDefine!!
            macroDefine = null
        } else if (myTokenType !in listOf(WHITE_SPACE, RIGHT_PAREN_MACRO, LINE_COMMENT, MULTILINE_COMMENT)) {
            macroDefine!!.elements.add(Pair(myTokenText, myTokenType))
        }
    }

    /**
     *
     */
    private fun addMacroParamToken() {
        if (myTokenType == LEFT_PAREN) {
            macroParamNestingLevel++
        } else if (myTokenType == RIGHT_PAREN) {
            macroParamNestingLevel--
        } else if (myTokenType == COMMA && macroParamNestingLevel == 1) {
            macroFuncParamIndex++
        } else {
            val paramName = macroFunc!!.params[macroFuncParamIndex]
            val param = macroFuncCallParams?.getOrPut(paramName) { mutableListOf() }
            param?.addIfNotNull(myTokenType)
        }
        if (macroParamNestingLevel == 0) {
            inMacroFuncCall = false
            macroExpansion = macroFunc
            macroExpansionIter = macroExpansion?.elements?.iterator()
        }
    }

    /**
     *
     */
    private fun isMacroCallStart(): Boolean {
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
