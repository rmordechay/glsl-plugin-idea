package glsl.plugin.psi.named.types.builtins

import com.intellij.lang.ASTNode
import glsl.plugin.psi.named.GlslNamedType
import glsl.plugin.psi.named.GlslNamedTypeImpl

abstract class GlslBuiltinType(node: ASTNode): GlslNamedTypeImpl(node) {
    /**
     *
     */
    override fun getAssociatedType(): GlslNamedType? {
        return this
    }
}