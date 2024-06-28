package glsl.plugin.utils

import com.intellij.lang.PsiBuilder
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
        val isCurrentIdentifier = builder.tokenType == GlslTypes.IDENTIFIER
        val isNextIdentifier = builder.lookAhead(1) == GlslTypes.IDENTIFIER
        if (isCurrentIdentifier && !isNextIdentifier) {
            builder.advanceLexer()
            return true
        }
        return false
    }

//    @JvmStatic
//    fun noSpace(builder: PsiBuilder, level: Int): Boolean {
//        var b = builder.lookAhead(0)
//        b = builder.lookAhead(1)
//        b = builder.lookAhead(2)
//        return builder.lookAhead(1) == WHITE_SPACE
//    }

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