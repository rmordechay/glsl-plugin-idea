package glsl.plugin.psi.named

import com.intellij.lang.ASTNode
import glsl.plugin.psi.GlslType
import glsl.plugin.psi.builtins.GlslBuiltinType
import glsl.plugin.psi.builtins.GlslMatrix
import glsl.plugin.psi.builtins.GlslScalar
import glsl.plugin.psi.builtins.GlslVector
import glsl.plugin.psi.named.types.GlslNamedStructSpecifier
import glsl.psi.impl.GlslTypeNameImpl
import glsl.psi.interfaces.GlslTypeSpecifier

/**
 *
 */
interface GlslNamedVariable : GlslNamedElement {
    /**
     *
     */
    fun getAssociatedType(): GlslType?

    /**
     *
     */
    fun getType(typeSpecifier: GlslTypeSpecifier): GlslType? {
        if (typeSpecifier.builtinTypeScalar != null) {
            return typeSpecifier.builtinTypeScalar as GlslScalar
        } else if (typeSpecifier.builtinTypeVector != null) {
            return typeSpecifier.builtinTypeVector as GlslVector
        } else if (typeSpecifier.builtinTypeMatrix != null) {
            return typeSpecifier.builtinTypeMatrix as GlslMatrix
        } else if (typeSpecifier.builtinTypeRest != null) {
            return typeSpecifier.builtinTypeRest as GlslBuiltinType
        } else if (typeSpecifier.structSpecifier != null) {
            return typeSpecifier.structSpecifier as GlslNamedStructSpecifier
        } else if (typeSpecifier.typeName != null) {
            return typeSpecifier.typeName as GlslTypeNameImpl
        }
        return null
    }
}

/**
 *
 */
abstract class GlslNamedVariableImpl(node: ASTNode) : GlslNamedElementImpl(node), GlslNamedVariable