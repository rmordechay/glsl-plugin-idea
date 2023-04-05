package generator;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.parser.GeneratedParserUtilBase;
import com.intellij.psi.tree.IElementType;

import static glsl.GlslTypes.MACRO_EXPANSION;
import static glsl.GlslTypes.PP_END;

public class GlslParserGenerator extends GeneratedParserUtilBase {

    /**
     *
     */
    public static boolean noSpace(PsiBuilder builder, int level) {
        char nextChar = builder.getOriginalText().charAt(builder.getCurrentOffset() - 1);
        return !Character.isWhitespace(nextChar);
    }

    /**
     *
     */
    public static boolean ppDefineToken(PsiBuilder builder, int level) {
        IElementType tokenType = builder.getTokenType();
        return tokenType != PP_END && consumeToken(builder, tokenType);
    }

    /**
     *
     */
    public static boolean consumeToken(PsiBuilder builder, IElementType token) {
        if (builder.getTokenType() == MACRO_EXPANSION) {
            builder.advanceLexer();
        }
        return GeneratedParserUtilBase.consumeToken(builder, token);
    }

    /**
     *
     */
    public static boolean nextTokenIs(PsiBuilder builder, IElementType token) {
        if (builder.getTokenType() == MACRO_EXPANSION) {
            builder.advanceLexer();
        }
        return GeneratedParserUtilBase.nextTokenIs(builder, token);
    }

    /**
     *
     */
    public static boolean nextTokenIs(PsiBuilder builder, String frameName, IElementType... tokens) {
        if (builder.getTokenType() == MACRO_EXPANSION) {
            builder.advanceLexer();
        }
        return GeneratedParserUtilBase.nextTokenIs(builder, frameName, tokens);
    }
}
