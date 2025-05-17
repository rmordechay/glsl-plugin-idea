package glsl.plugin.psi

import com.intellij.lang.PsiBuilder
import com.intellij.lang.parser.GeneratedParserUtilBase
import glsl.GlslTypes.*


object GlslPsiUtils : GeneratedParserUtilBase() {


    /**
     *  This method differentiates between a type and a variable. If we have 2 identifiers
     *  one after the other we know the first one must be a type and the second one a variable.
     *  Normally this method is not doing much since the lexer is doing this job already, but
     *  the lexer will fail if an undeclared user type is used and would make the whole parser crash.
     *  In such cases this method will secure correct parsing.
     */
    @JvmStatic
    fun primaryExprVariable(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "primary_expr_variable")) return false
        val isCurrentTokenIdentifier = builder.tokenType == IDENTIFIER
        val isNextTokenIdentifier = builder.lookAhead(1) == IDENTIFIER
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