package glsl.plugin.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.icons.AllIcons
import com.intellij.psi.TokenType
import com.intellij.psi.util.elementType
import com.intellij.psi.util.prevLeaf
import com.intellij.util.ProcessingContext
import glsl.GlslTypes
import glsl.data.GlslDefinitions
import glsl.data.GlslTokenSets
import glsl.plugin.language.GlslIcon
import glsl.plugin.utils.GlslBuiltinUtils
import glsl.plugin.utils.GlslUtils
import glsl.plugin.utils.GlslUtils.createLookupElement
import glsl.plugin.utils.GlslUtils.createLookupElements
import glsl.plugin.utils.GlslUtils.getVectorInsertHandler
import javax.swing.Icon


/**
 *
 */
abstract class GlslCompletionProvider : CompletionProvider<CompletionParameters>()

enum class GlslPostWhiteSpaceCompletion {
    Ignore,
    Require,
    Reject
}
/**
 *
 */
class GlslGenericCompletion(private vararg var keywords: String, private val icon: Icon? = null, private val whitespace: GlslPostWhiteSpaceCompletion = GlslPostWhiteSpaceCompletion.Ignore) : GlslCompletionProvider() {

    /**
     *
     */
    override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, resultSet: CompletionResultSet) {
        val prevLeaf = parameters.position.prevLeaf(true)
        val theResultSet = if (prevLeaf != null) {
            val prevElementType = prevLeaf.elementType
            when(whitespace) {
                GlslPostWhiteSpaceCompletion.Require -> if (prevElementType != TokenType.WHITE_SPACE) return
                GlslPostWhiteSpaceCompletion.Reject -> if (prevElementType == TokenType.WHITE_SPACE) return
                GlslPostWhiteSpaceCompletion.Ignore -> {}
            }
            when(prevElementType) {
                GlslTypes.INTCONSTANT -> resultSet.withPrefixMatcher(prevLeaf.text)
                else -> resultSet
            }
        } else {
            resultSet
        }
        theResultSet.addAllElements(keywords.map { createLookupElement(it, psiElement = parameters.position, icon = icon) })
    }
}

/**2
 *
 */
class GlslPpCompletion : GlslCompletionProvider() {

    private val preprocessors = GlslUtils.getTokenSetAsStrings(GlslTokenSets.PREPROCESSORS)
        .map { it.lowercase().replace("pp_", "") }
        .toTypedArray()

    private val insertHandler = InsertHandler<LookupElement> { context, lookupElement ->
        context.document.replaceString(context.startOffset, context.selectionEndOffset, "${lookupElement.lookupString} ")
    }

    /**
     *
     */
    override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, resultSet: CompletionResultSet) {
        resultSet.addAllElements(preprocessors.map {
            createLookupElement(it.drop(1), insertHandler, psiElement = parameters.position)
        })
    }
}


/**
 *
 */
class GlslBuiltinFuncCompletion : GlslCompletionProvider() {
    private val builtinFuncMap = GlslBuiltinUtils.getBuiltinFuncs()

    /**
     *
     */
    override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, resultSet: CompletionResultSet) {
        for ((funcName, funcOverloads) in builtinFuncMap) {
            val prefix = resultSet.prefixMatcher.prefix.lowercase()
            if (!funcName.lowercase().contains(prefix)) continue
            for (funcVariant in funcOverloads) {
                resultSet.addElement(GlslUtils.getFunctionLookupElement(funcVariant, GlslIcon.PLUGIN_FILE_ICON))
            }
        }
        val elements = GlslDefinitions.VEC_MAT_CONSTRUCTORS.map { createLookupElement(it, getVectorInsertHandler(), AllIcons.Nodes.Type) }
        resultSet.addAllElements(elements)
    }
}

/**
 *
 */
class GlslBuiltinTypesCompletion : GlslCompletionProvider() {
    private val tokens = GlslTokenSets.BUILTIN_TYPES.map { it.toString() }

    /**
     *
     */
    override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, resultSet: CompletionResultSet) {
        val builtinTypes = createLookupElements(tokens.toList())
        resultSet.addAllElements(builtinTypes)
    }
}

/**
 *
 */
class GlslIncludeStatementCompletion : GlslCompletionProvider() {
    private val insertHandler = InsertHandler<LookupElement> { context, lookupElement ->
        val s = lookupElement.lookupString
        context.document.replaceString(context.startOffset, context.selectionEndOffset, s)
        context.editor.caretModel.moveToOffset(context.startOffset + s.length)
    }

    /**
     *
     */
    override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, resultSet: CompletionResultSet) {
        val virtualFile = parameters.originalFile.virtualFile
        val parentDir = virtualFile.parent ?: return
        val lookupElements = mutableListOf<LookupElement>()
        for (siblingFile in parentDir.children) {
            val siblingFileName = siblingFile.name
            if (siblingFileName == virtualFile.name) continue
            if (siblingFile.isDirectory) {
                lookupElements.add(createLookupElement("$siblingFileName/", insertHandler))
            } else {
                lookupElements.add(createLookupElement(siblingFileName))
            }
        }
        resultSet.addAllElements(lookupElements)
    }
}

