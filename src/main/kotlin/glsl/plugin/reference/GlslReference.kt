package glsl.plugin.reference

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.impl.source.resolve.ResolveCache.AbstractResolver
import glsl.plugin.psi.GlslIdentifier
import glsl.plugin.psi.GlslVariable
import glsl.plugin.psi.named.GlslNamedVariable
import glsl.plugin.reference.FilterType.CONTAINS
import glsl.plugin.reference.FilterType.EQUALS


class StopLookupException : Exception()

enum class FilterType {
    EQUALS,
    CONTAINS
}

/**
 *
 */
abstract class GlslReference(private val element: GlslVariable, textRange: TextRange) : PsiReferenceBase<GlslIdentifier>(element, textRange) {
    abstract fun doResolve(filterType: FilterType = EQUALS)
    abstract fun shouldResolve(): Boolean
    internal var currentFilterType = EQUALS
    internal val resolvedReferences = arrayListOf<GlslNamedVariable>()
    internal var includeRecursionLevel = 0
    internal var project: Project? = null

    internal val resolver = AbstractResolver<GlslReference, GlslNamedVariable> { reference, _ ->
        reference.doResolve()
        reference.resolvedReferences.firstOrNull()
    }

    /**
     *
     */
    override fun resolve(): GlslNamedVariable? {
        if (!shouldResolve()) return null
        project = element.project
        val resolveCache = ResolveCache.getInstance(project!!)
        return resolveCache.resolveWithCaching(this, resolver, true, false)
    }

    /**
     *
     */
    override fun getVariants(): Array<LookupElement> {
        doResolve(CONTAINS)
        return resolvedReferences.mapNotNull { it.getLookupElement() }.toTypedArray()
    }

    /**
     *
     */
    override fun handleElementRename(newElementName: String): PsiElement? {
        return element.replaceElementName(newElementName)
    }
}