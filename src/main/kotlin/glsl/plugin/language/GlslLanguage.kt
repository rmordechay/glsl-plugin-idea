package glsl.plugin.language

import com.intellij.lang.Language
import com.intellij.psi.tree.IElementType


/**
 *
 */
class GlslLanguage : Language("Glsl") {
    companion object {
        val INSTANCE = GlslLanguage()
        val RIGHT_PAREN_MACRO_CALL = GlslTokenType("RIGHT_PAREN_MACRO_CALL")
        val LEFT_PAREN_MACRO_CALL = GlslTokenType("LEFT_PAREN_MACRO_CALL")
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


