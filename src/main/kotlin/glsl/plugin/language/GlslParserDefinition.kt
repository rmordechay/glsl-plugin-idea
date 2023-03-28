package glsl.plugin.language

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import glsl.GlslTypes
import glsl._GlslParser

/**
 *
 */
class GlslParserDefinition : ParserDefinition {

    /**
     *
     */
    override fun createLexer(project: Project?): Lexer {
        return GlslLexerAdapter()
    }

    /**
     *
     */
    override fun createParser(project: Project?): PsiParser {
        return _GlslParser()
    }

    /**
     *
     */
    override fun getFileNodeType(): IFileElementType {
        return IFileElementType(GlslLanguage.INSTANCE)
    }

    /**
     *
     */
    override fun getWhitespaceTokens(): TokenSet {
        return TokenSet.create(TokenType.WHITE_SPACE)
    }

    /**
     *
     */
    override fun getCommentTokens(): TokenSet {
        return TokenSet.create(GlslTypes.LINE_COMMENT, GlslTypes.MULTILINE_COMMENT)
    }

    /**
     *
     */
    override fun getStringLiteralElements(): TokenSet {
        return TokenSet.create(GlslTypes.STRING_LITERAL)
    }

    /**
     *
     */
    override fun createElement(node: ASTNode?): PsiElement {
        return GlslTypes.Factory.createElement(node)
    }

    /**
     *
     */
    override fun createFile(viewProvider: FileViewProvider): PsiFile {
        return GlslFile(viewProvider)
    }


    /**
     *
     */
    private fun replaceDirective(text: String): String {
        val defineNames = hashMapOf<String, String>()
        val lines = text.lines().toMutableList()
        for (i in lines.indices) {
            val line = lines[i]
            if (line.isEmpty()) continue
            if (line.startsWith("#define")) {
                val lineWithoutDefine = line.substring("#define ".length)
                val firstWordIndex = lineWithoutDefine.indexOf(' ')
                if (firstWordIndex == -1) continue
                val word = lineWithoutDefine.substring(0, firstWordIndex)
                val rest = lineWithoutDefine.substring(firstWordIndex)
                defineNames[word] = rest
                lines[i] = ""
            } else {
                for (entry in defineNames.entries) {
                    if (line.contains(entry.key)) {
                        lines[i] = line.replace(entry.key, entry.value)
                    }
                }
            }
        }

        return lines.joinToString("\n")
    }
}