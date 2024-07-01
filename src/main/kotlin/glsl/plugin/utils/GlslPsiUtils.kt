package glsl.plugin.utils

import com.intellij.lang.PsiBuilder
import com.intellij.psi.TokenType.WHITE_SPACE
import glsl.GlslTypes
import glsl.plugin.psi.GlslIdentifierImpl
import glsl.psi.interfaces.*
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
        val isCurrentTokenIdentifier = builder.tokenType == GlslTypes.IDENTIFIER
        val isNextTokenIdentifier = builder.lookAhead(1) == GlslTypes.IDENTIFIER
        if (isCurrentTokenIdentifier && !isNextTokenIdentifier) {
            builder.advanceLexer()
            return true
        }
        return false
    }

    @JvmStatic
    fun noSpace(builder: PsiBuilder, level: Int): Boolean {
        return builder.lookAhead(0) != WHITE_SPACE
    }

    /**
     *
     */
    fun getPostfixIdentifier(postfixExpr: GlslPostfixExpr?): GlslIdentifierImpl? {
        return when (postfixExpr) {
            is GlslPrimaryExpr -> postfixExpr.variableIdentifier as? GlslIdentifierImpl
            is GlslFunctionCall -> postfixExpr.variableIdentifier as? GlslIdentifierImpl
            is GlslPostfixArrayIndex -> getPostfixIdentifier(postfixExpr.postfixExpr)
            is GlslPostfixInc -> getPostfixIdentifier(postfixExpr.postfixExpr)
            else -> null
        }
    }
}