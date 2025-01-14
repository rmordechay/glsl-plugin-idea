package glsl.plugin.reference

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.impl.source.resolve.ResolveCache.AbstractResolver
import com.intellij.psi.util.PsiTreeUtil.getParentOfType
import com.intellij.psi.util.PsiTreeUtil.getPrevSiblingOfType
import glsl.plugin.psi.GlslType
import glsl.plugin.psi.named.GlslNamedElement
import glsl.plugin.psi.named.GlslNamedType
import glsl.plugin.reference.FilterType.CONTAINS
import glsl.plugin.utils.GlslUtils.getRealVirtualFile
import glsl.psi.interfaces.GlslDeclaration
import glsl.psi.interfaces.GlslExternalDeclaration
import glsl.psi.interfaces.GlslStatement

class GlslTypeReference(private val element: GlslType, textRange: TextRange) : GlslReference(element, textRange) {

    private val resolver = AbstractResolver<GlslTypeReference, GlslNamedType> { reference, _ ->
        reference.doResolve()
        reference.resolvedReferences.firstOrNull() as? GlslNamedType
    }

    /**
     *
     */
    override fun resolve(): GlslNamedType? {
        if (!shouldResolve()) return null
        project = element.project
        val resolveCache = ResolveCache.getInstance(project!!)
        return resolveCache.resolveWithCaching(this, resolver, true, false)
    }

    /**
     *
     */
    override fun getVariants(): Array<LookupElement> {
        project = element.project
        doResolve(CONTAINS)
        return resolvedReferences.mapNotNull { it.getLookupElement() }.toTypedArray()
    }

    /**
     *
     */
    override fun doResolve(filterType: FilterType) {
        try {
            currentFile = element.getRealVirtualFile()
            resolvedReferences.clear()
            currentFilterType = filterType
            resolveType()
        } catch (_: StopLookupException) { }
    }

    /**
     *
     */
    override fun shouldResolve(): Boolean {
        if (currentFilterType == CONTAINS && element.isEmpty()) return true
        return element.getDeclaration() == null
    }

    /**
     *
     */
    override fun resolveMany(): List<GlslNamedElement> {
        if (!shouldResolve()) return emptyList()
        project = element.project
        val resolveCache = ResolveCache.getInstance(project!!)
        resolveCache.resolveWithCaching(this, resolver, true, false)
        return resolvedReferences
    }

    /**
     *
     */
    private fun resolveType(): GlslNamedType? {
        var statementPrevSibling = getParentOfType(element, GlslStatement::class.java)
        while (statementPrevSibling != null) {
            resolveDeclarationType(statementPrevSibling.declaration)
            statementPrevSibling = getPrevSiblingOfType(statementPrevSibling, GlslStatement::class.java)
        }
        var externalDeclaration = getParentOfType(element, GlslExternalDeclaration::class.java)
        while (externalDeclaration != null) {
            externalDeclaration = getPrevSiblingOfType(externalDeclaration, GlslExternalDeclaration::class.java)
            lookupInExternalDeclaration(currentFile, externalDeclaration)
        }
        return null
    }

    /**
     *
     */
    override fun lookupInExternalDeclaration(relativeTo: VirtualFile?, externalDeclaration: GlslExternalDeclaration?) {
        lookupInIncludeDeclaration(relativeTo, externalDeclaration?.ppStatement?.ppIncludeDeclaration)
        resolveDeclarationType(externalDeclaration?.declaration)
    }

    /**
     *
     */
    private fun resolveDeclarationType(declaration: GlslDeclaration?) {
        if (declaration == null) return
        val structSpecifier = declaration.singleDeclaration?.typeSpecifier?.structSpecifier
        if (structSpecifier != null) {
            findReferenceInElement(structSpecifier)
        } else if (declaration.blockStructureWrapper != null) {
            findReferenceInElement(declaration.blockStructureWrapper?.blockStructure)
        }
    }
}