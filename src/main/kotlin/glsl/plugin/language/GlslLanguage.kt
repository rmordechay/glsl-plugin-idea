package glsl.plugin.language

import com.intellij.lang.ASTNode
import com.intellij.lang.Language
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.FlexAdapter
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import glsl.GlslTypes
import glsl.GlslTypes.Factory
import glsl._GlslLexer
import glsl._GlslParser

/**
 *
 */
class GlslLanguage : Language("Glsl") {
    companion object {
        val INSTANCE =  GlslLanguage()
    }
}

/**
 *
 */
class GlslTokenType(debugName: String) : IElementType(debugName, GlslLanguage.INSTANCE)

/**
 *
 */
class GlslElementType(debugName: String) : IElementType(debugName, GlslLanguage.INSTANCE)

/**
 *
 */
class GlslLexerAdapter : FlexAdapter(_GlslLexer(null))

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
        return IFileElementType( GlslLanguage.INSTANCE)
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
        return Factory.createElement(node)
    }

    /**
    *
    */
    override fun createFile(viewProvider: FileViewProvider): PsiFile {
        return GlslFile(viewProvider)
    }

}

