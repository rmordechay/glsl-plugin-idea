package glsl.plugin.language

import com.intellij.lexer.LexerBase
import com.intellij.openapi.vfs.readText
import com.intellij.psi.TokenType.WHITE_SPACE
import com.intellij.psi.search.FilenameIndex.getVirtualFilesByName
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.tree.IElementType
import com.intellij.util.containers.addIfNotNull
import glsl.GlslTypes.*
import glsl._GlslLexer
import glsl._GlslLexer.*
import glsl.plugin.language.GlslLanguage.Companion.LEFT_PAREN_MACRO_CALL
import glsl.plugin.language.GlslLanguage.Companion.RIGHT_PAREN_MACRO_CALL
import glsl.plugin.utils.GlslUtils

/**
 *
 */
class GlslMacro(val name: String, val macroDefineType: IElementType) {
    val elements = arrayListOf<Pair<String?, IElementType?>>()
    var params = mutableListOf<String>()
    var macroExpansionIter: Iterator<Pair<String?, IElementType?>>? = null
}

/**
 *
 */
class GlslLexer : LexerBase() {
    private val lexer = _GlslLexer()
    private val helperLexer = _GlslLexer()
    private var includeLexer: GlslLexer? = null

    private var myText: String = ""
    private var myBufferEnd: Int = 0
    private var myTokenType: IElementType? = null
    private var myTokenStart: Int = 0
    private var myTokenText: String = ""

    private var macros = hashMapOf<String, GlslMacro>()
    private var macroDefine: GlslMacro? = null
    private var macroExpansion: GlslMacro? = null

    private var inMacroFuncCall = false
    private var macroFuncCallParams: HashMap<String, MutableList<IElementType>>? = null
    private var paramExpansionIter: Iterator<IElementType>? = null
    private var macroFuncParamIndex: Int = 0
    private var macroParamNestingLevel: Int = 0
    private var macroFunc: GlslMacro? = null

    private var shouldExpandInclude = false
    private var includePaths: MutableList<String>? = null

    /**
     *
     */
    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        lexer.reset(buffer, startOffset, endOffset, initialState)
        myText = buffer.toString()
        myBufferEnd = endOffset
        macros.clear()
        advance()
    }

    /**
     *
     */
    override fun advance() {
        if (macroExpansion == null && !shouldExpandInclude) {
            advanceLexer()
        }

        if (isMacroCallStart()) {
            setMacroCallData()
        } else if (paramExpansionIter != null) {
            expandMacroParam()
            if (paramExpansionIter == null) expandMacro()
        } else if (macroExpansion != null) {
            expandMacro()
        } else if (inMacroFuncCall) {
            addMacroParamToken()
        } else if (state == MACRO_INCLUDE_STATE && myTokenType in listOf(INCLUDE_PATH, STRING_LITERAL)) {
            setIncludeLexer()
            return
        } else if (shouldExpandInclude) {
            expandInclude()
            return
        } else if (myTokenType == END_INCLUDE) {
            shouldExpandInclude = true
            return
        }

        if (state == MACRO_IDENTIFIER_STATE && myTokenType == IDENTIFIER) {
            macroDefine = GlslMacro(tokenText, getMacroType())
        } else if (state == MACRO_FUNC_DEFINITION_STATE && myTokenType == MACRO_FUNC_PARAM) {
            macroDefine!!.params.addIfNotNull(myTokenText)
        } else if (state == MACRO_BODY_STATE) {
            addMacroBodyToken()
        }
    }

    /**
     *
     */
    override fun getTokenType(): IElementType? {
        return myTokenType
    }

    /**
     *
     */
    override fun getTokenStart(): Int {
        return myTokenStart
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
    private fun advanceLexer() {
        myTokenType = lexer.advance()
        myTokenText = lexer.yytext().toString()
        myTokenStart = lexer.tokenStart
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
            macroExpansion!!.macroExpansionIter = macroExpansion?.elements?.iterator()
            myTokenType = MACRO_OBJECT
        }
    }

    /**
     *
     */
    private fun expandMacro() {
        if (state != MACRO_BODY_STATE && myTokenType in listOf(MACRO_OBJECT, RIGHT_PAREN_MACRO_CALL)) {
            myTokenStart += myTokenText.length
        }
        if (macroExpansion!!.macroExpansionIter?.hasNext() == true) {
            val nextMacroToken = macroExpansion!!.macroExpansionIter?.next() ?: return
            myTokenText = nextMacroToken.first!!
            myTokenType = nextMacroToken.second
            if (myTokenText in macroExpansion!!.params) {
                paramExpansionIter = macroFuncCallParams?.get(myTokenText)?.iterator()
                myTokenType = paramExpansionIter?.next()
            }
        } else {
            macroExpansion = null
            advanceLexer()
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
        } else if (myTokenType !in listOf(
                WHITE_SPACE,
                RIGHT_PAREN_MACRO,
                LINE_COMMENT,
                MULTILINE_COMMENT,
                MACRO_OBJECT,
                MACRO_FUNCTION
            )
        ) {
            macroDefine!!.elements.add(Pair(myTokenText, myTokenType))
        }
    }

    /**
     *
     */
    private fun addMacroParamToken() {
        if (macroParamNestingLevel == 0 && myTokenType == LEFT_PAREN) {
            myTokenType = LEFT_PAREN_MACRO_CALL
            macroParamNestingLevel++
        } else if (myTokenType == LEFT_PAREN) {
            macroParamNestingLevel++
        } else if (myTokenType == RIGHT_PAREN) {
            macroParamNestingLevel--
        }
        if (myTokenType == COMMA && macroParamNestingLevel == 1) {
            macroFuncParamIndex++
        } else if (macroParamNestingLevel >= 1 && myTokenType != LEFT_PAREN_MACRO_CALL) {
            val params = macroFunc?.params ?: return
            if (params.count() <= macroFuncParamIndex) return
            val paramName = params[macroFuncParamIndex]
            val param = macroFuncCallParams?.getOrPut(paramName) { mutableListOf() }
            param?.addIfNotNull(myTokenType)
        }

        if (macroParamNestingLevel == 0) {
            macroExpansion = macroFunc
            macroExpansion!!.macroExpansionIter = macroExpansion?.elements?.iterator()
            inMacroFuncCall = false
            macroFunc = null
            myTokenType = RIGHT_PAREN_MACRO_CALL
        }
    }

    /**
     *
     */
    private fun setIncludeLexer() {
        val pathString = if (tokenType == INCLUDE_PATH) tokenText else tokenText.replace("\"", "")
        val file = getVirtualFilesByName(pathString, GlobalSearchScope.allScope(GlslUtils.getProject()))
        val fileText = file.firstOrNull()?.readText() ?: return
        includeLexer = GlslLexer()
        includeLexer?.start(fileText, 0, fileText.length, YYINITIAL)
    }

    /**
     *
     */
    private fun expandInclude() {
        val nextToken = includeLexer?.tokenType
        if (nextToken == null) {
            shouldExpandInclude = false
            includeLexer = null
            advanceLexer()
        } else {
            myTokenType = nextToken
            myTokenText = includeLexer?.tokenText ?: ""
            includeLexer?.advance()
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
