package glsl.plugin.language

import com.intellij.lexer.LexerBase
import com.intellij.openapi.project.Project
import com.intellij.psi.TokenType.WHITE_SPACE
import com.intellij.psi.tree.IElementType
import com.intellij.util.containers.addIfNotNull
import glsl.GlslTypes.*
import glsl._GlslLexer
import glsl._GlslLexer.*
import glsl.plugin.psi.GlslInclude.Companion.isValidIncludePath
import glsl.plugin.utils.GlslUtils
import glsl.psi.interfaces.GlslExternalDeclaration
import java.io.File

class GlslLexerAdapter(val project: Project?, currentFileName: String? = null) : LexerBase() {
    private val lexer = _GlslLexer(null)
    private var userTypesTable = hashSetOf<String>()
    private var state = 0
    private var tokenType: IElementType? = null
    private var bufferSequence: CharSequence = ""
    private var tokenText = ""
    private var tokenStart = 0
    private var tokenEnd = 0
    private var bufferEnd = 0
    private val macrosTable = hashMapOf<String, MutableList<IElementType>>()
    private val includeFilesNames = hashSetOf<String>()

    private var macroExpansion: MacroExpansion? = null
    private var currentMacro: Pair<String, MutableList<IElementType>>? = null
    private var inPpFuncCall = false
    private var ppFuncCallName = ""

    init {
        if (currentFileName != null) {
            includeFilesNames.add(File(currentFileName).name)
        }
    }

    /**
     *
     */
    override fun getTokenType(): IElementType? {
        if (inPpFuncCall) {
            handlePpCallFuncExpansion()
        } else if (macroExpansion != null) {
            expandMacro()
        } else if (state == PREPROCESSOR_DEFINE) {
            addTokenToMacro()
        } else if (shouldExpandMacro()) {
            startPpExpansion()
        } else if (tokenType == PP_END) {
            addCurrentMacroToTable()
        }  else if (tokenType == PP_INCLUDE) {
            resolveInclude()
        } else if (tokenType == IDENTIFIER) {
            setIdentifier()
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
        if (macroExpansion != null) {
            if (tokenStart != tokenEnd) {
                tokenStart = tokenEnd
            }
            return
        }
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
        return tokenStart
    }

    /**
     *
     */
    override fun getTokenEnd(): Int {
        return tokenEnd
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
    private fun setIdentifier() {
        if (state in  listOf(PREPROCESSOR, PREPROCESSOR_DEFINE)) return
        if (lexer.afterType || lexer.afterDot) {
            lexer.reset()
        } else if (lexer.afterTypeQualifier) {
            lexer.reset()
            userTypesTable.add(tokenText)
            tokenType = TYPE_NAME_IDENTIFIER
        } else if (userTypesTable.contains(tokenText)) {
            lexer.afterType()
            tokenType = TYPE_NAME_IDENTIFIER
        }
    }

    /**
     *
     */
    private fun resolveInclude() {
        val includePath = peek()
        if (!isValidIncludePath(includePath)) return
        val fileName = File(includePath.substring(1, includePath.length - 1)).name
        if (includeFilesNames.contains(fileName)) return // Avoids recursion
        includeFilesNames.add(fileName)
        val psiFile = GlslUtils.getPsiFileByPath(project, includePath)
        val children = psiFile?.children ?: return
        for (child in children) {
            if (child !is GlslExternalDeclaration) continue
            val typeSpecifier = child.declaration?.singleDeclaration?.getAssociatedType()?.getTypeText() ?: continue
            userTypesTable.add(typeSpecifier)
        }
    }

    /**
     *
     */
    private fun startPpExpansion() {
        if (peek(false) == "(") {
            inPpFuncCall = true
            ppFuncCallName = tokenText
        } else {
            setMacroExpansion(tokenText)
        }
        tokenType = MACRO_EXPANSION
    }

    /**
     *
     */
    private fun addTokenToMacro() {
        if (lexer.afterDefine) {
            lexer.afterDefine = false
            currentMacro = Pair(peek(), mutableListOf())
        } else {
            currentMacro?.second?.addIfNotNull(tokenType)
        }
    }

    /**
     *
     */
    private fun addCurrentMacroToTable() {
        val identifier = currentMacro?.first ?: return
        var macro = currentMacro?.second ?: return
        if (macro.isEmpty()) return
        if (macro.first() == WHITE_SPACE) {
            macro.removeFirst()
        }
        if (macro.isEmpty()) return
        macro.removeFirst()
        if (macro.isEmpty()) return
        val isMacroFunc = macro.first() == LEFT_PAREN
        if (isMacroFunc) {
            val rightParenIndex = macro.lastIndexOf(RIGHT_PAREN)
            if (rightParenIndex != -1) {
                macro = macro.subList(rightParenIndex + 1, macro.size)
            }
        }
        macrosTable[identifier] = macro
        currentMacro = null
    }

    /**
     *
     */
    private fun expandMacro() {
        val nextToken = macroExpansion?.getNextToken()
        if (nextToken != null) {
            tokenType = nextToken
        } else {
            macroExpansion = null
            advance()
        }
    }

    /**
     *
     */
    private fun handlePpCallFuncExpansion() {
        if (tokenType == RIGHT_PAREN) {
            setMacroExpansion(ppFuncCallName)
            inPpFuncCall = false
            ppFuncCallName = ""
        }
        tokenType = MACRO_EXPANSION
    }

    /**
     *
     */
    private fun shouldExpandMacro(): Boolean {
        return state == YYINITIAL && macrosTable.containsKey(tokenText)
    }

    /**
     *
     */
    private fun setMacroExpansion(key: String) {
        val tokens = macrosTable[key] ?: return
        macroExpansion = MacroExpansion(tokens.iterator())
    }

    /**
     *
     */
    private fun peek(skipWhiteSpace: Boolean = true): String {
        val currentText = tokenText
        val currentState = state
        val currentTokenType = tokenType
        val currentTokenStart = tokenStart
        val currentTokenEnd = tokenEnd
        advance()
        if (skipWhiteSpace && tokenType == WHITE_SPACE) {
            advance()
        }
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
    inner class MacroExpansion(private val tokens: Iterator<IElementType>) {
        fun getNextToken(): IElementType? {
            if (tokens.hasNext()) {
                return tokens.next()
            }
            return null
        }
    }

}
