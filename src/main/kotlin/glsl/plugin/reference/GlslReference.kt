package glsl.plugin.reference

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.impl.source.resolve.ResolveCache.AbstractResolver
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference
import com.intellij.psi.util.PsiTreeUtil.getParentOfType
import com.intellij.psi.util.PsiTreeUtil.getPrevSiblingOfType
import glsl.plugin.psi.GlslIdentifier
import glsl.plugin.psi.GlslIdentifierImpl
import glsl.plugin.psi.GlslInclude
import glsl.plugin.psi.GlslType
import glsl.plugin.psi.named.GlslNamedElement
import glsl.plugin.reference.FilterType.CONTAINS
import glsl.plugin.reference.FilterType.EQUALS
import glsl.plugin.utils.GlslBuiltinUtils.getBuiltinConstants
import glsl.plugin.utils.GlslBuiltinUtils.getBuiltinFuncs
import glsl.plugin.utils.GlslBuiltinUtils.getShaderVariables
import glsl.plugin.utils.GlslElementManipulator
import glsl.plugin.utils.GlslUtils
import glsl.plugin.utils.GlslUtils.getPostfixIdentifier
import glsl.psi.interfaces.*


class StopLookupException : Exception()

enum class FilterType {
    EQUALS,
    CONTAINS
}

/**
 *
 */
class GlslReference(private val element: GlslIdentifierImpl, textRange: TextRange) : PsiReferenceBase<GlslIdentifier>(element, textRange) {

    private var currentFilterType = EQUALS
    private val resolvedReferences = arrayListOf<GlslNamedElement>()

    private val cacheResolver = AbstractResolver<GlslReference, GlslNamedElement> { reference, _ ->
        reference.doResolve()
        reference.resolvedReferences.firstOrNull()
    }

    /**
     *
     */
    override fun resolve(): GlslNamedElement? {
        val project = GlslUtils.getOpenProject()
        val resolveCache = ResolveCache.getInstance(project)
        return resolveCache.resolveWithCaching(this, cacheResolver, true, false)
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
    override fun handleElementRename(newElementName: String): PsiElement {
        return GlslElementManipulator().handleContentChange(myElement, rangeInElement, newElementName)
    }

    /**
     *
     */
    private fun doResolve(filterType: FilterType = EQUALS) {
        try {
            if (!shouldResolve()) return
            resolvedReferences.clear()
            currentFilterType = filterType
            lookupInPostfixStructMember()
            lookupInBuiltin()
            val externalDeclaration: GlslExternalDeclaration?
            val statement = getParentOfType(element, GlslStatement::class.java)
            if (statement != null) { // If true, we are inside a function (statements cannot occur outside).
                externalDeclaration = lookupInFunctionScope(statement)
            } else {
                externalDeclaration = getParentOfType(element, GlslExternalDeclaration::class.java)
            }
            lookupInGlobalScope(externalDeclaration)
        } catch (_: StopLookupException) {
        }
    }

    /**
     *
     */
    fun resolveType(): GlslType? {
        try {
            var statementPrevSibling = getParentOfType(element, GlslStatement::class.java)
            while (statementPrevSibling != null) {
                resolveDeclarationType(statementPrevSibling.declaration)
                statementPrevSibling = getPrevSiblingOfType(statementPrevSibling, GlslStatement::class.java)
            }
            var externalDeclaration = getParentOfType(element, GlslExternalDeclaration::class.java)
            while (externalDeclaration != null) {
                externalDeclaration = getPrevSiblingOfType(externalDeclaration, GlslExternalDeclaration::class.java)
                val declaration = externalDeclaration?.declaration
                resolveDeclarationType(declaration)
                val ppIncludeDeclaration = externalDeclaration?.ppStatement?.ppIncludeDeclaration
                if (ppIncludeDeclaration != null) {
                    lookupInPpIncludeDeclaration(ppIncludeDeclaration)
                }
            }
            return null
        } catch (_: StopLookupException) {
            return resolvedReferences.firstOrNull()?.getAssociatedType()
        }
    }

    /**
     *
     */
    private fun resolveDeclarationType(declaration: GlslDeclaration?) {
        if (declaration != null) {
            val structSpecifier = declaration.singleDeclaration?.typeSpecifier?.typeSpecifierUser?.structSpecifier
            if (structSpecifier != null) {
                findReferenceInElement(structSpecifier)
            } else if (declaration.blockStructureWrapper != null) {
                findReferenceInElement(declaration.blockStructureWrapper?.blockStructure)
            }
        }
    }

    /**
     *
     */
    private fun lookupInGlobalScope(externalDeclaration: GlslExternalDeclaration?) {
        if (externalDeclaration == null) return
        var edPrevSibling = getPrevSiblingOfType(externalDeclaration, GlslExternalDeclaration::class.java)
        while (edPrevSibling != null) {
            lookupInExternalDeclaration(edPrevSibling)
            edPrevSibling = getPrevSiblingOfType(edPrevSibling, GlslExternalDeclaration::class.java)
        }
    }

    /**
     *
     */
    private fun lookupInFunctionScope(statement: GlslStatement?): GlslExternalDeclaration? {
        if (statement == null) return null
        var prevScope = lookupInInnerScope(statement)
        while (prevScope != null) {
            when (prevScope) {
                is GlslStatement -> {
                    prevScope = lookupInInnerScope(prevScope)
                }
                is GlslFunctionDefinition -> { // The beginning of the function.
                    lookupInFunctionPrototype(prevScope.functionPrototype, true)
                    return prevScope.parent as GlslExternalDeclaration
                }
                else -> {
                    return null
                }
            }
        }
        return null
    }

    /**
     *
     */
    private fun lookupInInnerScope(statement: GlslStatement?): PsiElement? {
        if (statement == null) return null
        if (statement.iterationStatement != null) {
            lookupInDeclaration(statement.iterationStatement?.declaration)
        }

        var statementPrevSibling = getPrevSiblingOfType(statement, GlslStatement::class.java)
        while (statementPrevSibling != null) {
            lookupInStatement(statementPrevSibling)
            statementPrevSibling = getPrevSiblingOfType(statementPrevSibling, GlslStatement::class.java)
        }
        return getParentScope(statement)
    }

    /**
     *
     */
    private fun lookupInBuiltin() {
        findReferenceInElementMap(getShaderVariables())
        findReferenceInElementMap(getBuiltinConstants())
        val funcCall = element.parent
        if (funcCall !is GlslFunctionCall) return
        val elementName = element.name
        val builtinFuncs = getBuiltinFuncs()
        if (builtinFuncs.containsKey(elementName)) {
            lookupInBuiltinFuncs(builtinFuncs[elementName]!!, funcCall)
        }
    }

    /**
     *
     */
    private fun lookupInBuiltinFuncs(funcs: List<GlslFunctionPrototype>, funcCall: GlslFunctionCall) {
        val exprList = funcCall.exprNoAssignmentList
        for (func in funcs) {
            val paramsDeclarators = func.funcHeaderWithParams?.parameterDeclaratorList ?: emptyList()
            if (paramsDeclarators.isEmpty() && exprList.isEmpty()) {
                findReferenceInElement(func.functionHeader)
                return
            }
            if (paramsDeclarators.size != exprList.size) continue
            var matchedArgs = 0
            paramsDeclarators.zip(exprList).forEach { (paramDeclarator, expr) ->
                val callerType = expr.getExprType()
                val definitionType = paramDeclarator.getAssociatedType() ?: return@forEach
                if (callerType?.getTypeText() == definitionType.getTypeText()) {
                    matchedArgs += 1
                } else {
                    return@forEach
                }
            }
            if (matchedArgs == paramsDeclarators.size) {
                findReferenceInElement(func.functionHeader)
            }
        }
    }

    /**
     *
     */
    private fun lookupInExternalDeclaration(externalDeclaration: GlslExternalDeclaration?) {
        if (externalDeclaration == null) return
        lookupInFunctionPrototype(externalDeclaration.functionDefinition?.functionPrototype, false)
        lookupInDeclaration(externalDeclaration.declaration)
        lookupInPpStatement(externalDeclaration.ppStatement)
    }

    /**
     *
     */
    private fun lookupInStatement(statement: GlslStatement?) {
        if (statement == null) return
        lookupInDeclaration(statement.declaration)
        lookupInDeclaration(statement.iterationStatement?.declaration)
        lookupInPpStatement(statement.ppStatement)
    }

    /**
     *
     */
    private fun lookupInDeclaration(declaration: GlslDeclaration?) {
        if (declaration == null) return
        lookupInSingleDeclaration(declaration.singleDeclaration)
        lookupInBlockStructureWrapper(declaration.blockStructureWrapper)
        lookupInFunctionPrototype(declaration.functionPrototype, false)
        findReferenceInElementList(declaration.declarationIdentifierWrapperList)
        findReferenceInElementList(declaration.initDeclaratorVariableList)
    }

    /**
     *
     */
    private fun lookupInSingleDeclaration(singleDeclaration: GlslSingleDeclaration?) {
        if (singleDeclaration == null) return
        if (singleDeclaration.variableIdentifier != null) {
            findReferenceInElement(singleDeclaration)
        }
        val structSpecifier = singleDeclaration.typeSpecifier.typeSpecifierUser?.structSpecifier
        findReferenceInElement(structSpecifier)
    }

    /**
     *  According to the GLSL specification, if a block structure has a variable associated with it, then
     *  the struct members can be accessed without calling the variable. In contrast, if the block has no
     *  variable name associated with it, then all the members are accessible.
     */
    private fun lookupInBlockStructureWrapper(blockStructureWrapper: GlslBlockStructureWrapper?) {
        if (blockStructureWrapper == null) return
        findReferenceInElement(blockStructureWrapper)
        if (blockStructureWrapper.variableIdentifier == null) {
            val structMembers = blockStructureWrapper.blockStructure.getAssociatedType()?.getStructMembers()
            findReferenceInElementList(structMembers)
        }
    }

    /**
     *
     */
    private fun lookupInFunctionPrototype(functionPrototype: GlslFunctionPrototype?, withParams: Boolean) {
        if (functionPrototype == null) return
        findReferenceInElement(functionPrototype.functionHeader)
        if (withParams) {
            findReferenceInElementList(functionPrototype.funcHeaderWithParams?.parameterDeclaratorList)
        }
    }

    /**
     *
     */
    private fun lookupInPpStatement(ppStatement: GlslPpStatement?) {
        if (ppStatement == null) return
        lookupInPpIncludeDeclaration(ppStatement.ppIncludeDeclaration)
    }

    /**
     *
     */
    private fun lookupInPpIncludeDeclaration(ppIncludeDeclaration: GlslPpIncludeDeclaration?) {
        if (ppIncludeDeclaration == null) return
        val glslInclude = ppIncludeDeclaration.ppIncludePath as GlslInclude
        val reference = glslInclude.reference as FileReference
        val externalDeclarations = reference.resolve()?.children ?: return
        for (externalDeclaration in externalDeclarations) {
            if (externalDeclaration !is GlslExternalDeclaration) continue
            lookupInExternalDeclaration(externalDeclaration)
        }
    }

    /**
     *  This method tries to resolve struct chained calls (e.g. 'a.b.c'). In order to resolve such an expression with an
     *  arbitrary number of chained names, we have to check for every name its previous name, resolve it, and check if
     *  the member is indeed there. If we have nested expression (with more than one dot), then we need to pass the type
     *  of the previous to the next expression until we reach the element in question.
     *
     */
    private fun lookupInPostfixFieldSelection(postfixFieldSelection: GlslPostfixFieldSelection?) {
        if (postfixFieldSelection == null) return
        val rootExpr = getPostfixIdentifier(postfixFieldSelection.postfixExpr) ?: return
        val resolvedRootExpr = rootExpr.getAsNamedElement() ?: rootExpr.reference?.resolve()
        var nextMemberType = resolvedRootExpr?.getAssociatedType()

        val identifierList = postfixFieldSelection.postfixStructMemberList.map {
            if (it.functionCall != null) it.functionCall!!.variableIdentifier
            else it.variableIdentifier
        }
        val elementPosition = identifierList.indexOf(element as GlslVariableIdentifier)
        val elementParents = identifierList.subList(0, elementPosition + 1)

        for ((index, parent) in elementParents.withIndex()) {
            if (index == elementPosition) {
                if (element.isEmpty()) {
                    findReferenceInElementList(nextMemberType?.getStructMembers(), true)
                } else {
                    findReferenceInElementList(nextMemberType?.getStructMembers())
                }
                break
            }
            val structMemberName = parent?.getName() ?: return
            nextMemberType = nextMemberType
                ?.getStructMember(structMemberName)
                ?.getAssociatedType()
                ?: break
        }
    }

    /**
     *
     */
    private fun lookupInPostfixStructMember() {
        var postfixStructMember: GlslPostfixStructMember? = null
        if (element.parent is GlslPostfixStructMember) {
            postfixStructMember = element.parent as GlslPostfixStructMember
        } else if (element.parent?.parent is GlslPostfixStructMember) {
            postfixStructMember = element.parent.parent as GlslPostfixStructMember
        }
        if (postfixStructMember != null) {
            lookupInPostfixFieldSelection(postfixStructMember.parent as? GlslPostfixFieldSelection)
            throw StopLookupException()
        }
    }

    /**
     *  Gets the parent scope inside a function by traversing up the tree. A new scope is
     *  reached once a GlslStatement is encountered, otherwise returns GlslFunctionDefinition
     *  meaning the end of the function scope. GlslFunctionDefinition must be encountered
     *  sooner or later since statement must be a child of function_definition.
     *
     *  @return GlslStatement or GlslFunctionDefinition
     */
    private fun getParentScope(glslStatement: GlslStatement?): PsiElement? {
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
    private fun findReferenceInElement(namedElement: GlslNamedElement?) {
        val namedElementName = namedElement?.name ?: return
        val elementName = element.name
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
    private fun findReferenceInElementList(namedElementsList: List<GlslNamedElement>?, addAll: Boolean = false) {
        if (namedElementsList == null) return
        if (addAll) {
            resolvedReferences.addAll(namedElementsList)
        }
        for (namedElement in namedElementsList) {
            findReferenceInElement(namedElement)
        }
    }

    /**
     *
     */
    private fun findReferenceInElementMap(namedElementsMap: Map<String, GlslNamedElement>) {
        if (namedElementsMap.isEmpty()) return
        val elementName = element.name
        if (currentFilterType == EQUALS) {
            if (namedElementsMap.containsKey(element.name)) {
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

    /**
     *
     */
    private fun shouldResolve(): Boolean {
        if (currentFilterType == CONTAINS) {
            if (element.isEmpty()) {
                return true
            }
            return element.parent !is GlslNamedElement
        } else if (currentFilterType == EQUALS) {
            return element.parent !is GlslNamedElement
        }
        return false
    }
}