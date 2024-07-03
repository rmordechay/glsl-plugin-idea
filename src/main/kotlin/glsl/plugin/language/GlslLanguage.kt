package glsl.plugin.language

import com.intellij.lang.*
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType.WHITE_SPACE
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import glsl.GlslTypes.*
import glsl._GlslParser
import glsl.plugin.language.GlslLanguage.Companion.DUMMY_ELEMENT
import utils.GeneratedParserUtil.*


/**
 *
 */
class GlslLanguage : Language("Glsl") {
    companion object {
        val INSTANCE = GlslLanguage()
        val DUMMY_ELEMENT = GlslTokenType("DUMMY_ELEMENT")
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
class GlslParserAdapter : _GlslParser() {
    override fun parseLight(root: IElementType, originalBuilder: PsiBuilder) {
        val state = ErrorState()
        ErrorState.initState(state, originalBuilder, root.language, EXTENDS_SETS_)
        val builder = GlslPsiBuilder(originalBuilder, state, this)
        val marker = enter_section_(builder, 0, _COLLAPSE_, null)
        val result = parse_root_(root, builder)
        exit_section_(builder, 0, marker, root, result, true, TRUE_CONDITION)
    }
}


/**
 *
 */
class GlslParserDefinition : ParserDefinition {

    /**
     *
     */
    override fun createLexer(project: Project?): Lexer {
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
        return IFileElementType(GlslLanguage.INSTANCE)
    }

    /**
     *
     */
    override fun getWhitespaceTokens(): TokenSet {
        return TokenSet.create(WHITE_SPACE, DUMMY_ELEMENT)
    }

    /**
     *
     */
    override fun getCommentTokens(): TokenSet {
        return TokenSet.create(LINE_COMMENT, MULTILINE_COMMENT)
    }

    /**
     *
     */
    override fun getStringLiteralElements(): TokenSet {
        return TokenSet.create(STRING_LITERAL)
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
