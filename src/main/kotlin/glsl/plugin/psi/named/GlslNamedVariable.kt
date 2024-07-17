package glsl.plugin.psi.named

import com.intellij.lang.ASTNode

/**
 *
 */
interface GlslNamedVariable : GlslNamedElement {
    /**
     *
     */
    fun getAssociatedType(): GlslNamedType?
}

/**
 *
 */
abstract class GlslNamedVariableImpl(node: ASTNode) : GlslNamedElementImpl(node), GlslNamedVariable