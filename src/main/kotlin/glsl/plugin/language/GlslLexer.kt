package glsl.plugin.language

import com.intellij.lexer.LexerBase
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.readText
import com.intellij.psi.TokenType.WHITE_SPACE
import com.intellij.psi.tree.IElementType
import com.intellij.util.containers.addIfNotNull
import glsl.GlslTypes.*
import glsl._GlslLexer
import glsl._GlslLexer.*
import glsl.data.GlslTokenSets.IGNORE_MACRO_BODY_TOKEN
import glsl.plugin.utils.GlslUtils.getVirtualFile

/**
 *
 */
class GlslMacro(val name: String, val macroDefineType: IElementType) {
    val elements = arrayListOf<Pair<String?, IElementType?>>()
    var params = mutableListOf<String>()
    var macroExpansionIter: Iterator<Pair<String?, IElementType?>>? = null
}

private const val RECURSION_LEVEL_LIMIT = 50000

/**
 *
 */
class GlslLexer(private val project: Project? = null, private val baseFile: VirtualFile? = null) : LexerBase() {
    private val lexer = _GlslLexer()
    private val helperLexerPool = ArrayList<_GlslLexer>()

    private var myTokenType: IElementType? = null
    private var myText = ""
    private var myBufferEnd = 0
    private var myTokenStart = 0
    private var myTokenText = ""

    private var macros = hashMapOf<String, GlslMacro>()
    private var macroDefine: GlslMacro? = null
    private var macroExpansion: GlslMacro? = null

    private var inMacroFuncCall = false
    private var macroFuncCallParams: HashMap<String, MutableList<IElementType>>? = null
    private var paramExpansionIter: Iterator<IElementType>? = null
    private var macroFuncParamIndex: Int = 0
    private var macroParamNestingLevel: Int = 0
    private var macroFunc: GlslMacro? = null

    private var recursionLevel = 0

    private fun getHelperLexer() = helperLexerPool.removeLastOrNull() ?: _GlslLexer()

    private fun freeHelperLexer(lexer: _GlslLexer) {
        lexer.reset(null, 0, 0, YYINITIAL)
        if (helperLexerPool.size < 4) {
            helperLexerPool.add(lexer)
        }
    }

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
        if (macroExpansion == null) {
            advanceLexer()
        }

        if (isMacroCallStart()) {
            initializeMacroCall()
        } else if (paramExpansionIter != null) {
            expandMacroParam()
            if (paramExpansionIter == null) expandMacro()
        } else if (macroExpansion != null) {
            expandMacro()
            if (inMacroFuncCall) addMacroParamToken()
        } else if (inMacroFuncCall) {
            addMacroParamToken()
        } else if (myTokenType in listOf(STRING_LITERAL, INCLUDE_PATH)) {
            addIncludeUserTypes()
        }

        if (state == MACRO_IDENTIFIER_STATE && myTokenType == IDENTIFIER) {
            myTokenType = getMacroType() ?: return
            macroDefine = GlslMacro(tokenText, myTokenType!!)
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
        //TODO this can trigger in huge shader files even when there's no recursion
        if (inEndlessRecursion()) return null
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
    private fun addIncludeUserTypes() {
        addIncludeRecursiveStep(lexer, baseFile, myTokenText, HashSet<VirtualFile>(), 0)
    }

    private fun addIncludeRecursiveStep(outLexer: _GlslLexer, relativeTo: VirtualFile?, tokenText: String, encounteredFiles: MutableSet<VirtualFile>, depth: Int) {
        val path = tokenText.replace("\"", "")
        if (path.isEmpty()) return
        val vf = getVirtualFile(path, relativeTo, project) ?: return
        if (encounteredFiles.contains(vf) || depth > 256) //256 unique recursive includes is crazy enough anyways
            return
        encounteredFiles.add(vf)
        val fileText = vf.readText()
        val helperLexer = getHelperLexer()
        helperLexer.reset(fileText, 0, fileText.length, YYINITIAL)
        while (true) {
            val helperTokenType = helperLexer.advance()
            if (helperTokenType == null) {
                break
            }
            if (helperTokenType in listOf(STRING_LITERAL, INCLUDE_PATH)) {
                val helperTokenText = helperLexer.yytext().toString()
                addIncludeRecursiveStep(helperLexer, vf, helperTokenText, encounteredFiles, depth + 1)
            }
        }
        outLexer.userDefinedTypes.addAll(helperLexer.userDefinedTypes)
        freeHelperLexer(helperLexer)
    }

    /**
     *
     */
    private fun initializeMacroCall() {
        val macro = macros[tokenText] ?: return
        if (macro.macroDefineType == MACRO_FUNCTION) {
            inMacroFuncCall = true
            macroParamNestingLevel = 0
            macroFuncParamIndex = 0
            macroFuncCallParams = hashMapOf()
            macroFunc = macro
            myTokenType = MACRO_FUNCTION_CALL
        } else if (macro.macroDefineType == MACRO_OBJECT) {
            myTokenType = MACRO_OBJECT_CALL
            macroExpansion = macro
            macroExpansion!!.macroExpansionIter = macroExpansion?.elements?.iterator()
        }
    }

    /**
     *
     */
    private fun expandMacro() {
        if (state != MACRO_BODY_STATE && myTokenType in listOf(MACRO_OBJECT_CALL, RIGHT_PAREN_MACRO_CALL)) {
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
        if (macroDefine == null) return
        if (myTokenType == PP_END) {
            lexer.yybegin(YYINITIAL);
            macros[macroDefine!!.name] = macroDefine!!
            macroDefine = null
        } else if (myTokenType !in IGNORE_MACRO_BODY_TOKEN && myTokenText != macroDefine?.name) {
            macroDefine?.elements?.add(Pair(myTokenText, myTokenType))
        }
    }

    /**
     * Collects the arguments of a macro call and saves them for later interpolation.
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
            if (params.size <= macroFuncParamIndex) return
            val paramName = params[macroFuncParamIndex]
            macroFuncCallParams
                ?.getOrPut(paramName, ::mutableListOf)
                ?.addIfNotNull(myTokenType)
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
     * Represents a start of a macro call
     */
    private fun isMacroCallStart(): Boolean {
        return myTokenType == IDENTIFIER &&
                myTokenText in macros &&
                state !in (listOf(MACRO_IDENTIFIER_STATE, MACRO_IGNORE_STATE)) &&
                !(macroExpansion != null && macroExpansion!!.name == myTokenText)
    }

    /**
     * Checks if the lexer is stuck in an infinite loop, and clears the data in case it is.
     */
    private fun inEndlessRecursion(): Boolean {
        if (recursionLevel++ < RECURSION_LEVEL_LIMIT) return false
        recursionLevel = 0
        clearAllData()
        return true
    }

    /**
     *
     */
    private fun clearAllData() {
        macros.clear()
        macroDefine = null
        macroExpansion = null
        inMacroFuncCall = false
        macroFuncCallParams = null
        paramExpansionIter = null
        macroFuncParamIndex = 0
        macroParamNestingLevel = 0
        macroFunc = null
        advanceLexer()
    }

    /**
     *
     */
    private fun getMacroType(): IElementType? {
        val helperLexer = getHelperLexer()
        helperLexer.reset(myText, tokenEnd, bufferEnd, YYINITIAL)
        val nextToken = helperLexer.advance()
        freeHelperLexer(helperLexer)
        if (nextToken == LEFT_PAREN) {
            lexer.yybegin(MACRO_FUNC_DEFINITION_STATE)
            return MACRO_FUNCTION
        } else if (nextToken == WHITE_SPACE) {
            lexer.yybegin(MACRO_BODY_STATE)
            return MACRO_OBJECT
        }
        return null
    }
}
