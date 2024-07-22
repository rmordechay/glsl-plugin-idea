package glsl.plugin.psi.named

import com.intellij.lang.ASTNode

/**
 *
 */
interface GlslNamedVariable : GlslNamedElement

/**
 *
 */
abstract class GlslNamedVariableImpl(node: ASTNode) : GlslNamedElementImpl(node), GlslNamedVariable