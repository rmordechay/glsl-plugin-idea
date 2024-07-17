package glsl.plugin.psi.named

import com.intellij.lang.ASTNode
import glsl.plugin.psi.named.types.builtins.GlslBuiltinRest
import glsl.plugin.psi.named.types.builtins.GlslMatrix
import glsl.plugin.psi.named.types.builtins.GlslScalar
import glsl.plugin.psi.named.types.builtins.GlslVector
import glsl.plugin.psi.named.types.user.GlslNamedStructSpecifier
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
            return typeSpecifier.builtinTypeRest as GlslBuiltinRest
        } else if (typeSpecifier.structSpecifier != null) {
            return typeSpecifier.structSpecifier as GlslNamedStructSpecifier
        }
//        else if (typeSpecifier.userType != null) {
//            return typeSpecifier.userType?.reference?.resolve() as? GlslNamedType
//        }
        return null
    }
}

/**
 *
 */
abstract class GlslNamedVariableImpl(node: ASTNode) : GlslNamedElementImpl(node), GlslNamedVariable