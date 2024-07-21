package glsl.plugin.code.formatter

import com.intellij.formatting.*
import com.intellij.formatting.FormattingModelProvider.createFormattingModelForPsiFile
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.tree.TokenSet
import glsl.GlslTypes.*
import glsl.data.GlslTokenSets.ADD_OPERATORS
import glsl.data.GlslTokenSets.BITWISE_OPERATORS
import glsl.data.GlslTokenSets.EQUALITY_OPERATORS
import glsl.data.GlslTokenSets.KEYWORDS
import glsl.data.GlslTokenSets.LOGICAL_OPERATORS
import glsl.data.GlslTokenSets.MUL_OPERATORS
import glsl.data.GlslTokenSets.NUMBER_SET
import glsl.data.GlslTokenSets.RELATIONAL_OPERATORS
import glsl.data.GlslTokenSets.SHIFT_OPERATORS
import glsl.data.GlslTokenSets.UNARY_OPERATORS
import glsl.plugin.language.GlslLanguage


class GlslFormattingModelBuilder : FormattingModelBuilder {


    /**
     *
     */
    override fun createModel(formattingContext: FormattingContext): FormattingModel {
        val codeStyleSettings = formattingContext.codeStyleSettings
        val glslBlock = GlslBlock(
            formattingContext.node,
            Wrap.createWrap(WrapType.NONE, false),
            null,
            createSpaceBuilder(codeStyleSettings),
            Indent.getNoneIndent()
        )
        return createFormattingModelForPsiFile(formattingContext.containingFile, glslBlock, codeStyleSettings)
    }

    /**
     *
     */
    private fun createSpaceBuilder(settings: CodeStyleSettings): SpacingBuilder {
        val commonSettings = settings.getCommonSettings(GlslLanguage.INSTANCE.id)
        commonSettings.SPACE_BEFORE_COLON = false
        return SpacingBuilder(settings, GlslLanguage.INSTANCE)
            .after(SEMICOLON).spaceIf(commonSettings.SPACE_AFTER_SEMICOLON)
            .before(SEMICOLON).spaceIf(commonSettings.SPACE_BEFORE_SEMICOLON)
            .before(LEFT_BRACKET).none()
            .before(ARRAY_SPECIFIER).none()
            .after(LEFT_PAREN_MACRO_CALL).none()
            .before(RIGHT_PAREN_MACRO).none()
            .before(RIGHT_PAREN_MACRO_CALL).none()
            .between(PP_VERSION, INTCONSTANT).spaces(1)
            .afterInside(LEFT_ANGLE, PP_INCLUDE_DECLARATION).none()
            .beforeInside(RIGHT_ANGLE, PP_INCLUDE_DECLARATION).none()
            .around(TYPE_QUALIFIER).spaces(1)
            .between(RIGHT_PAREN, TYPE_QUALIFIER).spaces(1)
            .between(RIGHT_PAREN, TYPE_QUALIFIER).spaces(1)
            .between(TYPE_QUALIFIER, TYPE_SPECIFIER).spaces(1)
            .between(TYPE_SPECIFIER, STRUCT_DECLARATOR).spaces(1)
            .between(TYPE_SPECIFIER, VARIABLE_IDENTIFIER).spaces(1)
            .between(BLOCK_STRUCTURE, VARIABLE_IDENTIFIER).spaces(1)
            .between(STRUCT_SPECIFIER, VARIABLE_IDENTIFIER).spaces(1)
            .after(COMMA).spaceIf(commonSettings.SPACE_AFTER_COMMA)
            .before(COMMA).spaceIf(commonSettings.SPACE_BEFORE_COMMA)
            .aroundInside(TokenSet.create(QUESTION, COLON), CONDITIONAL_EXPR).spaces(1)
            .after(COLON).spaceIf(commonSettings.SPACE_AFTER_COLON)
            .before(COLON).spaceIf(commonSettings.SPACE_BEFORE_COLON)
            .after(QUESTION).spaceIf(commonSettings.SPACE_AFTER_QUEST)
            .before(QUESTION).spaceIf(commonSettings.SPACE_BEFORE_QUEST)
            .between(LAYOUT, LEFT_PAREN).spaces(1)
            .between(TYPE_SPECIFIER, LEFT_PAREN).none()
            .between(RIGHT_PAREN, LEFT_BRACE).spaces(1)
            .between(RIGHT_PAREN, COMPOUND_STATEMENT).spaces(1)
            .between(RIGHT_PAREN, COMPOUND_STATEMENT_NO_NEW_SCOPE).spaces(1)
            .between(FUNCTION_DECLARATOR, COMPOUND_STATEMENT_NO_NEW_SCOPE).spaces(1)
            .between(VARIABLE_IDENTIFIER, LEFT_PAREN).none()
            .withinPair(LEFT_PAREN, RIGHT_PAREN).spaceIf(commonSettings.SPACE_WITHIN_PARENTHESES)
            .withinPair(LEFT_BRACKET, RIGHT_BRACKET).spaceIf(commonSettings.SPACE_WITHIN_BRACKETS)
            .withinPairInside(LEFT_BRACE, RIGHT_BRACE, SINGLE_DECLARATION).spaces(1)
            .between(TYPE_NAME, LEFT_BRACE).spaces(1)
            .between(VARIABLE_IDENTIFIER, FUNC_HEADER_WITH_PARAMS).none()
            .betweenInside(TokenSet.create(DASH), NUMBER_SET, ADD_EXPR).spaces(1)
            .between(DASH, PRIMARY_EXPR).none()
            .between(DASH, FUNCTION_CALL).none()
            .between(DASH, POSTFIX_INC).none()
            .between(DASH, POSTFIX_ARRAY_INDEX).none()
            .between(DASH, POSTFIX_FIELD_SELECTION).none()
            .around(KEYWORDS).spaces(1)
            .between(KEYWORDS, SEMICOLON).none()
            .around(EQUAL).spaces(1)
            .around(ASSIGNMENT_OPERATOR).spaceIf(commonSettings.SPACE_AROUND_ASSIGNMENT_OPERATORS)
            .around(UNARY_OPERATORS).spaceIf(commonSettings.SPACE_AROUND_UNARY_OPERATOR)
            .around(ADD_OPERATORS).spaceIf(commonSettings.SPACE_AROUND_ADDITIVE_OPERATORS)
            .around(MUL_OPERATORS).spaceIf(commonSettings.SPACE_AROUND_MULTIPLICATIVE_OPERATORS)
            .around(EQUALITY_OPERATORS).spaceIf(commonSettings.SPACE_AROUND_EQUALITY_OPERATORS)
            .around(RELATIONAL_OPERATORS).spaceIf(commonSettings.SPACE_AROUND_RELATIONAL_OPERATORS)
            .around(LOGICAL_OPERATORS).spaceIf(commonSettings.SPACE_AROUND_LOGICAL_OPERATORS)
            .around(BITWISE_OPERATORS).spaceIf(commonSettings.SPACE_AROUND_BITWISE_OPERATORS)
            .around(SHIFT_OPERATORS).spaceIf(commonSettings.SPACE_AROUND_SHIFT_OPERATORS)
    }
}
