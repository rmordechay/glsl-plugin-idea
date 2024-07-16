package glsl.plugin.psi.named

import com.intellij.lang.ASTNode
import glsl.plugin.psi.named.builtins.GlslMatrix
import glsl.plugin.psi.named.builtins.GlslScalar
import glsl.plugin.psi.named.builtins.GlslVector
import glsl.plugin.psi.named.types.GlslNamedStructSpecifier
import glsl.psi.interfaces.GlslTypeSpecifier

/**
 *
 */
interface GlslNamedVariable : GlslNamedElement {
    /**
     *
     */
    fun getAssociatedType(): GlslNamedType?

    /**
     *
     */
    fun getType(typeSpecifier: GlslTypeSpecifier): GlslNamedType? {
        if (typeSpecifier.builtinTypeScalar != null) {
            return typeSpecifier.builtinTypeScalar as GlslScalar
        } else if (typeSpecifier.builtinTypeVector != null) {
            return typeSpecifier.builtinTypeVector as GlslVector
        } else if (typeSpecifier.builtinTypeMatrix != null) {
            return typeSpecifier.builtinTypeMatrix as GlslMatrix
        } else if (typeSpecifier.builtinTypeRest != null) {
            return typeSpecifier.builtinTypeRest as GlslNamedBuiltinType
        } else if (typeSpecifier.structSpecifier != null) {
            return typeSpecifier.structSpecifier as GlslNamedStructSpecifier
        } else if (typeSpecifier.typeName != null) {
            return typeSpecifier.typeName?.reference?.resolve() as? GlslNamedType
        }
        return null
    }
}

/**
 *
 */
abstract class GlslNamedVariableImpl(node: ASTNode) : GlslNamedElementImpl(node), GlslNamedVariable