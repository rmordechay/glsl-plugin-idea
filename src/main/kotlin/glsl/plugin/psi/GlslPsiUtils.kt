package glsl.plugin.psi

import com.intellij.lang.PsiBuilder
import glsl.GlslTypes
import glsl.GlslTypes.*
import utils.GeneratedParserUtil


object GlslPsiUtils : GeneratedParserUtil() {


    /**
     *  This method differentiates between a type and a variable. If we have 2
     *  identifiers one after the other we know the first one is the type and the
     *  second one is the variable. Without this check it's impossible to detect
     *  when IDENTIFIER is a type and when is a variable.
     */
    @JvmStatic
    fun primaryExprVariable(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "primary_expr_variable")) return false
        val isCurrentTokenIdentifier = builder.tokenType == GlslTypes.IDENTIFIER
        val isNextTokenIdentifier = builder.lookAhead(1) == GlslTypes.IDENTIFIER
        if (isCurrentTokenIdentifier && !isNextTokenIdentifier) {
            builder.advanceLexer()
            return true
        }
        return false
    }

    /**
     *
     */
    @JvmStatic
    fun macroIdentifierObject(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "macro_identifier_object")) return false
        val marker = enter_section_(builder, level, _NONE_, VARIABLE_IDENTIFIER, "<variable identifier>")
        val result = consumeToken(builder, MACRO_OBJECT)
        exit_section_(builder, level, marker, result, false, null)
        return result
    }

    /**
     *
     */
    @JvmStatic
    fun macroIdentifierFunction(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "macro_identifier_function")) return false
        val marker = enter_section_(builder, level, _NONE_, VARIABLE_IDENTIFIER, "<variable identifier>")
        val result = consumeToken(builder, MACRO_FUNCTION)
        exit_section_(builder, level, marker, result, false, null)
        return result
    }

    /**
     *
     */
    @JvmStatic
    fun macroBodyToken(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "macro_body_token")) return false
        val isPpEndOrNull = builder.tokenType == PP_END || builder.tokenType == null
        if (isPpEndOrNull) return false
        builder.advanceLexer()
        return true
    }

    /**
     *
     */
    @JvmStatic
    fun ppText(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "pp_text")) return false
        while (builder.tokenType != PP_END && builder.tokenType != null) {
            builder.advanceLexer()
        }
        return true
    }
}