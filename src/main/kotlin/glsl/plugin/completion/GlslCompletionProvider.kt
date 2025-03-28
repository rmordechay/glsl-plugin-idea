package glsl.plugin.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.icons.AllIcons
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.TokenType
import com.intellij.psi.util.elementType
import com.intellij.psi.util.prevLeaf
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import com.intellij.util.ProcessingContext
import glsl.GlslTypes
import glsl.data.GlslDefinitions
import glsl.data.GlslTokenSets
import glsl.plugin.language.GlslIcon
import glsl.plugin.utils.GlslBuiltinUtils
import glsl.plugin.utils.GlslUtils
import glsl.plugin.utils.GlslUtils.createLookupElement
import glsl.plugin.utils.GlslUtils.createLookupElements
import glsl.plugin.utils.GlslUtils.getRealVirtualFile
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

/**
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
        val elem = context.file.findElementAt(context.startOffset)
        if (elem != null && elem.elementType == GlslTypes.STRING_LITERAL) {
            context.document.replaceString(elem.startOffset + 1, elem.endOffset - 1, s)
        } else {
            context.document.replaceString(context.startOffset, context.selectionEndOffset, s)
            context.editor.caretModel.moveToOffset(context.startOffset + s.length)
        }
    }

    /**
     *
     */
    override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, resultSet: CompletionResultSet) {
        val virtualFile = parameters.originalFile.getRealVirtualFile() ?: return
        var path = parameters.position.text.trim('"', ' ').replace("IntellijIdeaRulezzz", "")
        val res = (if (path.startsWith('/'))
            resolveOptiFineAbsolutePathDirectory(path, virtualFile)
        else
            resolveRelativeDirectory(path, virtualFile.parent, virtualFile)) ?: return
        path = res.first
        val parentDir = res.second
        val isSameDir = res.third
        val lookupDirs = mutableListOf<LookupElement>()
        val lookupFiles = mutableListOf<LookupElement>()
        parameters.offset
        for (siblingFile in parentDir.children) {
            val siblingFileName = siblingFile.name
            if (isSameDir && siblingFileName == virtualFile.name) continue

            if (siblingFile.isDirectory) {
                lookupDirs.add(createLookupElement("$path$siblingFileName/", returnTypeText = "directory", psiElement = parameters.position, insertHandler = insertHandler))
            } else {
                val type = if (siblingFileName.contains('.')) when(siblingFileName.substring(siblingFileName.lastIndexOf('.'))) {
                    ".vert", ".vsh" -> "vertex"
                    ".frag", ".fsh" -> "fragment"
                    ".geom", ".gsh" -> "geometry"
                    ".tesc" -> "tess control"
                    ".tese" -> "tess eval"
                    ".comp" -> "compute"
                    ".glsl" -> "shader"
                    else -> ""
                } else ""
                lookupFiles.add(createLookupElement("$path$siblingFileName", returnTypeText = type, psiElement = parameters.position, insertHandler = insertHandler))
            }
        }
        resultSet.addAllElements(lookupDirs)
        resultSet.addAllElements(lookupFiles)
    }

    private fun resolveOptiFineAbsolutePathDirectory(p: String, virtualFile: VirtualFile): Triple<String, VirtualFile, Boolean>? {
        val root = GlslUtils.resolveSourceRoot(virtualFile) ?: return null
        return resolveRelativeDirectory(p, root, virtualFile)
    }

    private fun resolveRelativeDirectory(p: String, startDir: VirtualFile, virtualFile: VirtualFile): Triple<String, VirtualFile, Boolean>? {
        var path = p;
        var addPathStartingSlash = false
        if (path.startsWith('/')) {
            path = path.substring(1)
            addPathStartingSlash = true
        }
        if (!path.contains('/') && path != ".")
            path = ""
        else {
            val si = path.lastIndexOf('/')
            path = path.substring(0, si)
        }
        val parentDir: VirtualFile
        var addStartingSlash = false
        var isSameDir = false
        if (!path.isEmpty() && !path.endsWith("/")) {
            addStartingSlash = true
        }
        if (path.isEmpty() || path == ".") {
            parentDir = startDir
        } else {
            val relFile = startDir.findFileByRelativePath(path) ?: return null
            parentDir = if (relFile.isDirectory) relFile else relFile.parent ?: return null
        }
        if (parentDir == virtualFile.parent) {
            isSameDir = true
        }
        if (addStartingSlash) {
            path = "$path/"
        }
        if (addPathStartingSlash) {
            path = "/$path"
        }
        return Triple(path, parentDir, isSameDir)
    }
}

