package glsl.plugin.utils

import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.ASTFactory
import com.intellij.lang.ASTFactory.leaf
import com.intellij.openapi.editor.EditorModificationUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import glsl.GlslTypes.BUILTIN_TYPE_SCALAR
import glsl.data.ShaderType
import glsl.plugin.psi.named.GlslNamedType
import glsl.plugin.psi.named.types.builtins.GlslBuiltinRest
import glsl.plugin.psi.named.types.builtins.GlslMatrix
import glsl.plugin.psi.named.types.builtins.GlslScalar
import glsl.plugin.psi.named.types.builtins.GlslVector
import glsl.plugin.psi.named.types.user.GlslNamedStructSpecifier
import glsl.psi.impl.GlslBuiltinTypeScalarImpl
import glsl.psi.interfaces.GlslFunctionDeclarator
import glsl.psi.interfaces.GlslPpIncludeDeclaration
import glsl.psi.interfaces.GlslTypeSpecifier
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.swing.Icon

/**
 *
 */
object GlslUtils {

    /**
     *
     */
    @JvmStatic
    fun getResourceFileAsString(path: String): String? {
        val file = GlslUtils::class.java.classLoader.getResourceAsStream(path)
        if (file != null) {
            val reader = BufferedReader(InputStreamReader(file))
            return reader.lines().toArray().joinToString("\n")
        }
        return null
    }


    /**
     *
     */
    @JvmStatic
    fun createLookupElement(
        lookupString: String,
        insertHandler: InsertHandler<LookupElement>? = null,
        icon: Icon? = null,
        returnTypeText: String? = null,
        withBoldness: Boolean = false,
        psiElement: PsiElement? = null,
    ): LookupElement {
        return LookupElementBuilder.create(lookupString)
            .withIcon(icon)
            .withTypeText(returnTypeText)
            .withInsertHandler(insertHandler)
            .withCaseSensitivity(false)
            .withBoldness(withBoldness)
            .withPsiElement(psiElement)
    }

    /**
     *
     */
    @JvmStatic
    fun createLookupElements(
        tokenList: List<String>,
        insertHandler: InsertHandler<LookupElement>? = null,
        icon: Icon? = null,
        returnTypeText: String? = null
    ): List<LookupElement> {
        return tokenList.map { createLookupElement(it, insertHandler, icon, returnTypeText) }
    }

    /**
     *
     */
    @JvmStatic
    fun getFunctionLookupElement(func: GlslFunctionDeclarator, icon: Icon?): LookupElement {
        var typeQualifierStr = func.typeQualifier?.text ?: ""
        // If type qualifier text has multiple words, take the first one
        if (typeQualifierStr.contains(" ")) {
            typeQualifierStr = typeQualifierStr.substring(0, typeQualifierStr.indexOf(' '))
        }
        val typeSpecifierStr = func.getAssociatedType()?.name ?: ""
        val fullySpecifiedType = "$typeQualifierStr $typeSpecifierStr".trim()
        val funcName = func.variableIdentifier.text
        var funcHeader = "$funcName("
        val funcHeaderWithParams = func.funcHeaderWithParams
        var hasParams = false
        if (funcHeaderWithParams != null) {
            funcHeader += funcHeaderWithParams.text
            hasParams = true
        }
        return createLookupElement(
            "$funcHeader)",
            getFunctionInsertHandler(hasParams),
            icon,
            fullySpecifiedType
        )
    }

    /**
     *
     */
    @JvmStatic
    fun getFunctionInsertHandler(hasParams: Boolean = true): InsertHandler<LookupElement> {
        return InsertHandler { context, item ->
            val document = context.document
            val funcName = removeArgsFromFuncText(item.lookupString)
            val offset = context.editor.caretModel.offset
            val text = document.getText(TextRange(offset, offset + 1))
            if (text == "(") {
                document.replaceString(context.startOffset, context.selectionEndOffset, funcName)
            } else {
                document.replaceString(context.startOffset, context.selectionEndOffset, "$funcName()")
                if (!hasParams) return@InsertHandler
                EditorModificationUtil.moveCaretRelatively(context.editor, -1)
            }
        }
    }

    /**
     *
     */
    @JvmStatic
    fun getVectorInsertHandler(): InsertHandler<LookupElement> {
        return InsertHandler { context, item ->
            val document = context.document
            val funcName = item.lookupString
            val offset = context.editor.caretModel.offset
            val text = document.getText(TextRange(offset, offset + 1))
            if (text == "(") {
                document.replaceString(context.startOffset, context.selectionEndOffset, funcName)
            } else {
                document.replaceString(context.startOffset, context.selectionEndOffset, "$funcName()")
                EditorModificationUtil.moveCaretRelatively(context.editor, +1)
            }
        }
    }

    /**
     *
     */
    @JvmStatic
    fun getVirtualFile(targetPathString: String?, baseFile: VirtualFile?, project: Project?): VirtualFile? {
        if (project == null || baseFile == null || targetPathString == null) return null
        return baseFile.parent?.findFileByRelativePath(targetPathString)
    }

    /**
     *
     */
    @JvmStatic
    fun getTokenSetAsStrings(tokenSet: TokenSet): Array<String> {
        return tokenSet.types.map { it.toString() }.toTypedArray()
    }

    /**
     *
     */
    @JvmStatic
    fun removeArgsFromFuncText(funcText: String): String {
        return funcText.replace("\\(.*\\)".toRegex(), "")
    }

    /**
     *
     */
    @JvmStatic
    fun isShaderFile(element: PsiElement): Boolean {
        val extension = element.containingFile.virtualFile.extension
        return enumValues<ShaderType>().any { extension?.lowercase() == it.name.lowercase() }
    }

    /**
     *
     */
    @JvmStatic
    fun getType(typeSpecifier: GlslTypeSpecifier?): GlslNamedType? {
        if (typeSpecifier == null) return null
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
        } else if (typeSpecifier.typeName != null) {
            return typeSpecifier.typeName?.resolveReference() as? GlslNamedType
        }
        return null
    }

    /**
     *
     */
    @JvmStatic
    fun createScalarTypeElement(elementType: IElementType, text: String): GlslBuiltinTypeScalarImpl {
        val composite = ASTFactory.composite(BUILTIN_TYPE_SCALAR)
        composite.rawAddChildren(leaf(elementType, text))
        return GlslBuiltinTypeScalarImpl(composite)
    }

    /**
     *
     */
    @JvmStatic
    fun getPathStringFromInclude(includeDeclaration: GlslPpIncludeDeclaration): String? {
        return if (includeDeclaration.stringLiteral != null) {
            includeDeclaration.stringLiteral!!.text.replace("\"", "")
        } else if (includeDeclaration.includePath != null) {
            includeDeclaration.includePath!!.text
        } else {
            return null
        }
    }
}

