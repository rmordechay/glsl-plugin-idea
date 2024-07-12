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
import glsl.data.GlslTokenSets

/**
 *
 */
class GlslParserDefinition : ParserDefinition {

    /**
     *
     */
    override fun createLexer(project: Project): Lexer {
        return GlslLexer()
    }

    /**
     *
     */
    override fun createParser(project: Project?): PsiParser {
        return GlslParserAdapter()
    }

    /**
     *
     */
    override fun getFileNodeType(): IFileElementType {
        return GlslIFileElementType()
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
        return GlslTokenSets.COMMENTS
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
}