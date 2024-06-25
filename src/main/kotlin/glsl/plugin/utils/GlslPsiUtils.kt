package glsl.plugin.utils

import com.intellij.lang.PsiBuilder
import com.intellij.lang.parser.GeneratedParserUtilBase
import com.intellij.psi.TokenType.WHITE_SPACE
import glsl.GlslTypes
import glsl.plugin.psi.GlslIdentifierImpl
import glsl.psi.interfaces.*


object GlslPsiUtils : GeneratedParserUtilBase() {


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

    @JvmStatic
    fun noSpace(builder: PsiBuilder, level: Int): Boolean {
        var b = builder.lookAhead(0)
        b = builder.lookAhead(1)
        b = builder.lookAhead(2)
        return builder.lookAhead(1) == WHITE_SPACE
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