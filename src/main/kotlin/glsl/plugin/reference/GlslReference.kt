package glsl.plugin.reference

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import glsl.plugin.psi.GlslIdentifier
import glsl.plugin.psi.named.GlslNamedElement
import glsl.plugin.psi.named.GlslNamedVariable
import glsl.plugin.reference.FilterType.CONTAINS
import glsl.plugin.reference.FilterType.EQUALS
import glsl.psi.interfaces.GlslFunctionDefinition
import glsl.psi.interfaces.GlslStatement


class StopLookupException : Exception()

enum class FilterType {
    EQUALS,
    CONTAINS
}

/**
 *
 */
abstract class GlslReference(private val element: GlslIdentifier, textRange: TextRange) : PsiReferenceBase<GlslIdentifier>(element, textRange) {
    abstract fun doResolve(filterType: FilterType = EQUALS)
    abstract fun shouldResolve(): Boolean
    abstract override fun resolve(): GlslNamedElement?

    protected var currentFilterType = EQUALS
    protected var project: Project? = null
    val resolvedReferences = arrayListOf<GlslNamedElement>()

    /**
     *
     */
    override fun handleElementRename(newElementName: String): PsiElement? {
        return element.replaceElementName(newElementName)
    }


    /**
     *  Gets the parent scope inside a function by traversing up the tree. A new scope is
     *  reached once a GlslStatement is encountered, otherwise returns GlslFunctionDefinition
     *  meaning the end of the function scope. GlslFunctionDefinition must be encountered
     *  sooner or later since statement must be a child of function_definition.
     *
     *  @return GlslStatement or GlslFunctionDefinition
     */
    protected fun getParentScope(glslStatement: GlslStatement?): PsiElement? {
        var elementParent = glslStatement?.parent
        while (elementParent != null && elementParent !is GlslStatement) {
            elementParent = elementParent.parent
            if (elementParent is GlslFunctionDefinition) {
                return elementParent
            }
        }
        return elementParent
    }

    /**
     *
     */
    protected fun findReferenceInElement(namedElement: GlslNamedElement?) {
        val namedElementName = namedElement?.name ?: return
        val elementName = element.getName()
        if (currentFilterType == EQUALS) {
            if (namedElementName != elementName) return
            resolvedReferences.add(namedElement)
            throw StopLookupException()
        } else if (currentFilterType == CONTAINS && namedElementName.contains(elementName)) {
            resolvedReferences.add(namedElement)
        }
    }

    /**
     *
     */
    protected fun findReferenceInElementList(namedElementsList: List<GlslNamedVariable>?, addAll: Boolean = false) {
        if (namedElementsList == null) return
        if (addAll) {
            resolvedReferences.addAll(namedElementsList)
            return
        }
        for (namedElement in namedElementsList) {
            findReferenceInElement(namedElement)
        }
    }

    /**
     *
     */
    protected fun findReferenceInElementMap(namedElementsMap: Map<String, GlslNamedVariable>) {
        if (namedElementsMap.isEmpty()) return
        val elementName = element.getName()
        if (currentFilterType == EQUALS) {
            if (namedElementsMap.containsKey(element.getName())) {
                findReferenceInElement(namedElementsMap[elementName])
            }
        } else if (currentFilterType == CONTAINS) {
            for ((key, value) in namedElementsMap.entries) {
                if (key.contains(elementName)) {
                    findReferenceInElement(value)
                }
            }
        }
    }
}