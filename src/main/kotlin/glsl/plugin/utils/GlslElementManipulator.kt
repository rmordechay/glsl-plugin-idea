package glsl.plugin.utils

import com.intellij.openapi.util.TextRange
import com.intellij.psi.AbstractElementManipulator
import glsl.plugin.psi.GlslIdentifier

/**
 *
 */
class GlslElementManipulator : AbstractElementManipulator<GlslIdentifier>() {
    /**
    *
    */
    override fun handleContentChange(element: GlslIdentifier, range: TextRange, newContent: String?): GlslIdentifier {
        return element.replaceElementName(newContent) ?: element
    }
}