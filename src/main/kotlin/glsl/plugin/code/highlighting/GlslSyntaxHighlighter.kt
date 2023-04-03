package glsl.plugin.code.highlighting

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors.*
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import glsl.GlslTypes
import glsl.data.GlslTokenSets
import glsl.plugin.code.highlighting.GlslTextAttributes.BAD_CHARACTER_TEXT_ATTR
import glsl.plugin.code.highlighting.GlslTextAttributes.BOOLEAN_TEXT_ATTR
import glsl.plugin.code.highlighting.GlslTextAttributes.BUILTIN_TYPE_TEXT_ATTR
import glsl.plugin.code.highlighting.GlslTextAttributes.DIRECTIVE_TEXT_ATTR
import glsl.plugin.code.highlighting.GlslTextAttributes.KEYWORD_TEXT_ATTR
import glsl.plugin.code.highlighting.GlslTextAttributes.LINE_COMMENT_TEXT_ATTR
import glsl.plugin.code.highlighting.GlslTextAttributes.MULTILINE_COMMENT_TEXT_ATTR
import glsl.plugin.code.highlighting.GlslTextAttributes.NUMBERS_TEXT_ATTR
import glsl.plugin.code.highlighting.GlslTextAttributes.OPERATORS_TEXT_ATTR
import glsl.plugin.code.highlighting.GlslTextAttributes.STRING_TEXT_ATTR
import glsl.plugin.language.GlslLexerAdapter


/**
 *
 */
class GlslSyntaxHighlighter(val project: Project? = null, val path: String? = null) : SyntaxHighlighterBase() {

    /**
    *
    */
    override fun getHighlightingLexer(): Lexer {
        return GlslLexerAdapter(project, path)
    }

    /**
    *
    */
    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        return pack(mapTokenToTextAttr(tokenType))
    }

    /**
     *
     */
    private fun mapTokenToTextAttr(tokenType: IElementType): TextAttributesKey? {
        return when (tokenType) {
            GlslTypes.LINE_COMMENT -> LINE_COMMENT_TEXT_ATTR
            GlslTypes.MULTILINE_COMMENT -> MULTILINE_COMMENT_TEXT_ATTR
            GlslTypes.STRING_LITERAL -> STRING_TEXT_ATTR
            GlslTypes.BOOLCONSTANT -> BOOLEAN_TEXT_ATTR
            in GlslTokenSets.ALL_OPERATORS -> OPERATORS_TEXT_ATTR
            in GlslTokenSets.DIRECTIVES -> DIRECTIVE_TEXT_ATTR
            in GlslTokenSets.NUMBER_SET -> NUMBERS_TEXT_ATTR
            in GlslTokenSets.KEYWORDS -> KEYWORD_TEXT_ATTR
            in GlslTokenSets.BUILTIN_TYPES -> BUILTIN_TYPE_TEXT_ATTR
            TokenType.BAD_CHARACTER -> BAD_CHARACTER_TEXT_ATTR
            else -> null
        }
    }
}

/**
 *
 */
object GlslTextAttributes {
    val VARIABLE_TEXT_ATTR = createTextAttributesKey("GLSL_VARIABLE", IDENTIFIER)
    val USER_DEFINED_TYPE_TEXT_ATTR = createTextAttributesKey("GLSL_USER_DEFINED_TYPE", CLASS_NAME)
    val BUILTIN_NAME_TEXT_ATTR = createTextAttributesKey("GLSL_BUILTIN_NAME_TEXT_ATTR", IDENTIFIER)
    val BUILTIN_GLOBAL_CONSTANTS = createTextAttributesKey("GLSL_BUILTIN_GLOBAL_CONSTANTS", CONSTANT)
    val STRING_TEXT_ATTR = createTextAttributesKey("GLSL_STRING", STRING)
    val BOOLEAN_TEXT_ATTR = createTextAttributesKey("GLSL_BOOLEAN", KEYWORD)
    val OPERATORS_TEXT_ATTR = createTextAttributesKey("GLSL_OPERATORS", OPERATION_SIGN)
    val DIRECTIVE_TEXT_ATTR = createTextAttributesKey("GLSL_DIRECTIVE", KEYWORD)
    val PP_DEFINE_DECLARATION = createTextAttributesKey("GLSL_PP_DEFINE_DECLARATION", CONSTANT)
    val STRUCT_TYPE_TEXT_ATTR = createTextAttributesKey("GLSL_STRUCT_IDENTIFIER", CLASS_NAME)
    val NUMBERS_TEXT_ATTR = createTextAttributesKey("GLSL_NUMBER", NUMBER)
    val KEYWORD_TEXT_ATTR = createTextAttributesKey("GLSL_KEYWORD", KEYWORD)
    val FUNC_TEXT_ATTR = createTextAttributesKey("GLSL_FUNCTION", FUNCTION_CALL)
    val FUNC_PARAM_TEXT_ATTR = createTextAttributesKey("GLSL_FUNCTION_PARAM", PARAMETER)
    val BUILTIN_TYPE_TEXT_ATTR = createTextAttributesKey("GLSL_BUILTIN_TYPE", CLASS_NAME)
    val LINE_COMMENT_TEXT_ATTR = createTextAttributesKey("GLSL_LINE_COMMENT", LINE_COMMENT)
    val MULTILINE_COMMENT_TEXT_ATTR = createTextAttributesKey("GLSL_MULTILINE_COMMENT", LINE_COMMENT)
    val BAD_CHARACTER_TEXT_ATTR = createTextAttributesKey("GLSL_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)
}

/**
 *
 */
class GlslSyntaxHighlightingFactory : SyntaxHighlighterFactory() {
    /**
    *
    */
    override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?): SyntaxHighlighter {
        return GlslSyntaxHighlighter(project, virtualFile?.path)
    }
}