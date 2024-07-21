package glsl.plugin.reference
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.impl.source.resolve.ResolveCache.AbstractResolver
import com.intellij.psi.util.PsiTreeUtil.getParentOfType
import com.intellij.psi.util.PsiTreeUtil.getPrevSiblingOfType
import com.intellij.psi.util.childrenOfType
import com.intellij.psi.util.elementType
import glsl.GlslTypes.MACRO_FUNCTION
import glsl.GlslTypes.MACRO_OBJECT
import glsl.plugin.psi.GlslVariable
import glsl.plugin.psi.named.GlslNamedElement
import glsl.plugin.psi.named.GlslNamedVariable
import glsl.plugin.psi.named.types.user.GlslNamedBlockStructure
import glsl.plugin.reference.FilterType.CONTAINS
import glsl.plugin.utils.GlslBuiltinUtils.getBuiltinConstants
import glsl.plugin.utils.GlslBuiltinUtils.getBuiltinFuncs
import glsl.plugin.utils.GlslBuiltinUtils.getShaderVariables
import glsl.plugin.utils.GlslUtils.getPsiFile
import glsl.psi.impl.GlslFunctionDeclaratorImpl
import glsl.psi.interfaces.*

private const val INCLUDE_RECURSION_LIMIT = 1000

class GlslVariableReference(private val element: GlslVariable, textRange: TextRange) : GlslReference(element, textRange) {
    private var includeRecursionLevel = 0

    private val resolver = AbstractResolver<GlslReference, GlslNamedVariable> { reference, _ ->
        reference.doResolve()
        reference.resolvedReferences.firstOrNull() as? GlslNamedVariable
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
    override fun doResolve(filterType: FilterType) {
        try {
            resolvedReferences.clear()
            currentFilterType = filterType
            lookupInPostfixStructMember()
            lookupInBuiltin()
            val statement = getParentOfType(element, GlslStatement::class.java)
            val externalDeclaration: GlslExternalDeclaration? =
                if (statement != null) { // If true, we are inside a function (statements cannot occur outside).
                    lookupInFunctionScope(statement)
                } else {
                    getParentOfType(element, GlslExternalDeclaration::class.java)
                }
            lookupInGlobalScope(externalDeclaration)
        } catch (_: StopLookupException) { }
    }

    /**
     *
     */
    override fun shouldResolve(): Boolean {
        if (currentFilterType == CONTAINS && element.isEmpty()) {
            return true
        }
        return element.parent !is GlslNamedVariable || element.firstChild.elementType in listOf(MACRO_OBJECT, MACRO_FUNCTION)
    }

    /**
     *
     */
    private fun lookupInGlobalScope(externalDeclaration: GlslExternalDeclaration?) {
        if (externalDeclaration == null) return
        var prevSibling = getPrevSiblingOfType(externalDeclaration, GlslExternalDeclaration::class.java)
        while (prevSibling != null) {
            lookupInExternalDeclaration(prevSibling)
            prevSibling = getPrevSiblingOfType(prevSibling, GlslExternalDeclaration::class.java)
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
                    lookupInFunctionDeclarator(prevScope.functionDeclarator, true)
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
        resolveBuiltinFunc()
    }

    /**
     *
     */
    private fun resolveBuiltinFunc() {
        val funcCall = element.parent
        if (funcCall !is GlslFunctionCall) return
        val elementName = element.name
        val builtinFuncs = getBuiltinFuncs()[elementName] ?: return
        val exprTypes = funcCall.exprNoAssignmentList.map { it.getExprType() }
        for (func in builtinFuncs) {
            val paramTypes = (func as? GlslFunctionDeclaratorImpl)?.getParameterTypes() ?: continue
            if (paramTypes.size != exprTypes.size) continue
            val typesMatch = paramTypes.zip(exprTypes).all { it.first.isEqual(it.second) }
            if (typesMatch) {
                findReferenceInElement(func)
            }
        }
        findReferenceInElementList(builtinFuncs, true)
    }

    /**
     *
     */
    private fun lookupInExternalDeclaration(externalDeclaration: GlslExternalDeclaration?) {
        if (externalDeclaration == null) return
        lookupInFunctionDeclarator(externalDeclaration.functionDefinition?.functionDeclarator, false)
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
        lookupInFunctionDeclarator(declaration.functionDeclarator, false)
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
            val blockStructure = blockStructureWrapper.blockStructure as? GlslNamedBlockStructure ?: return
            val structMembers = blockStructure.getStructMembers()
            findReferenceInElementList(structMembers)
        }
    }

    /**
     *
     */
    private fun lookupInFunctionDeclarator(functionDeclarator: GlslFunctionDeclarator?, withParams: Boolean) {
        if (functionDeclarator == null) return
        findReferenceInElement(functionDeclarator)
        if (withParams) {
            findReferenceInElementList(functionDeclarator.funcHeaderWithParams?.parameterDeclaratorList)
        }
    }

    /**
     *
     */
    private fun lookupInPpStatement(ppStatement: GlslPpStatement?) {
        if (ppStatement == null) return
        lookupInIncludeDeclaration(ppStatement.ppIncludeDeclaration)
        findReferenceInElement(ppStatement.ppDefineObject)
        findReferenceInElement(ppStatement.ppDefineFunction)
    }

    /**
     *
     */
    private fun lookupInIncludeDeclaration(ppIncludeDeclaration: GlslPpIncludeDeclaration?) {
        if (ppIncludeDeclaration == null) return
        includeRecursionLevel++
        val path = if (ppIncludeDeclaration.stringLiteral != null) {
            ppIncludeDeclaration.stringLiteral!!.text.replace("\"", "")
        } else if (ppIncludeDeclaration.includePath != null) {
            ppIncludeDeclaration.includePath!!.text
        } else {
            return
        }
        if (includeRecursionLevel >= INCLUDE_RECURSION_LIMIT) {
            includeRecursionLevel = 0
            throw StopLookupException()
        }
        val psiFile = getPsiFile(path, project)
        val externalDeclarations = psiFile?.childrenOfType<GlslExternalDeclaration>()
        if (externalDeclarations != null) {
            for (externalDeclaration in externalDeclarations) {
                lookupInExternalDeclaration(externalDeclaration)
            }
        }
        includeRecursionLevel--
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
        var nextMemberType = rootExpr.reference?.resolve()?.getAssociatedType() ?: return

        val identifierList = postfixFieldSelection.postfixStructMemberList.map {
            if (it.functionCall != null) it.functionCall!!.variableIdentifier
            else it.variableIdentifier
        }
        val elementPosition = identifierList.indexOf(element as GlslVariableIdentifier)
        val elementParents = identifierList.subList(0, elementPosition + 1)

        for ((index, parent) in elementParents.withIndex()) {
            if (index == elementPosition) {
                val structMembers = nextMemberType.getStructMembers()
                if (element.isEmpty()) {
                    findReferenceInElementList(structMembers, true)
                } else {
                    findReferenceInElementList(structMembers)
                }
                break
            }
            val structMemberName = parent?.getName() ?: return
            val structMember = nextMemberType.getStructMember(structMemberName)
            nextMemberType = structMember?.getAssociatedType() ?: break
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
     *
     */
    private fun getPostfixIdentifier(postfixExpr: GlslPostfixExpr?): GlslVariable? {
        return when (postfixExpr) {
            is GlslPrimaryExpr -> postfixExpr.variableIdentifier as? GlslVariable
            is GlslFunctionCall -> postfixExpr.variableIdentifier as? GlslVariable
            is GlslPostfixArrayIndex -> getPostfixIdentifier(postfixExpr.postfixExpr)
            is GlslPostfixInc -> getPostfixIdentifier(postfixExpr.postfixExpr)
            else -> null
        }
    }
}