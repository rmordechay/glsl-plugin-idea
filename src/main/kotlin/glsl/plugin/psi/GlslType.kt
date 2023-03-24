package glsl.plugin.psi

import com.intellij.psi.PsiElement
import glsl.data.GlslDefinitions
import glsl.plugin.psi.builtins.GlslBuiltinType
import glsl.plugin.psi.builtins.GlslMatrix
import glsl.plugin.psi.builtins.GlslScalar
import glsl.plugin.psi.builtins.GlslVector
import glsl.plugin.psi.named.GlslNamedElement
import glsl.plugin.reference.GlslReference
import glsl.psi.interfaces.GlslTypeSpecifier

/**
 *
 */
interface GlslType {
    fun getStructMembers(): List<GlslNamedElement>
    fun getStructMember(memberName: String): GlslNamedElement?
    fun getTypeText(): String?
    fun isConvertible(other: String): Boolean
    fun getDimension(): Int
    fun getBinaryExprType(rightExprType: GlslType, expr: PsiElement? = null): GlslType?

    /**
     *
     */
    fun isEqual(other: GlslType?): Boolean {
        if (other == null) return false
        val otherTypeText = other.getTypeText() ?: return false
        return getTypeText() == otherTypeText || isConvertible(otherTypeText)
    }

    /**
     *
     */
    fun isEqual(other: String?): Boolean {
        if (other == null) return false
        return getTypeText() == other || isConvertible(other)
    }

    companion object {

        /**
         *
         */
        fun getInstance(typeSpecifier: GlslTypeSpecifier?): GlslType? {
            if (typeSpecifier == null) return null
            val builtinType = typeSpecifier.typeSpecifierBuiltin
            if (builtinType != null) {
                if (isScalar(builtinType)) {
                    return GlslScalar(builtinType.text)
                } else if (isVec(builtinType)) {
                    return GlslVector(builtinType.text)
                } else if (isMatrix(builtinType)) {
                    return GlslMatrix(typeSpecifier)
                } else {
                    return GlslBuiltinType(builtinType)
                }
            }
            val userType = typeSpecifier.typeSpecifierUser ?: return null
            if (userType.structSpecifier != null) {
                return userType.structSpecifier
            } else if (userType.typeName != null) {
                val reference = userType.typeName?.reference as GlslReference
                return reference?.resolveType()
            }
            return null
        }

        /**
         *
         */
        private fun isScalar(typeText: PsiElement?): Boolean {
            return typeText != null && GlslDefinitions.SCALARS.containsKey(typeText.text)
        }

        /**
         *
         */
        private fun isVec(typeText: PsiElement?): Boolean {
            return typeText != null && GlslDefinitions.VECTORS.containsKey(typeText.text)
        }

        /**
         *
         */
        private fun isMatrix(typeText: PsiElement?): Boolean {
            return typeText != null && GlslDefinitions.MATRICES.containsKey(typeText.text)
        }
    }
}