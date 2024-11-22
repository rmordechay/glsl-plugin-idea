package glsl.plugin.editor

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler
import com.intellij.psi.TokenType
import glsl.GlslTypes

class GlslQuoteHandler: SimpleTokenSetQuoteHandler(GlslTypes.STRING_LITERAL, TokenType.BAD_CHARACTER)