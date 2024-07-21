package glsl.plugin.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTFactory
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceService
import com.intellij.psi.impl.source.tree.ChangeUtil
import com.intellij.psi.util.PsiTreeUtil
import glsl.GlslTypes
import glsl.GlslTypes.Factory
import glsl.plugin.language.GlslFile
import glsl.plugin.language.GlslFileType
import glsl.plugin.reference.GlslVariableReference
import glsl.plugin.utils.GlslUtils

/**
 *
 */
abstract class GlslVariable(node: ASTNode) : ASTWrapperPsiElement(node), GlslIdentifier {
    /**
    *
    */
    override fun getReferences(): Array<out PsiReference?> {
        return PsiReferenceService.getService().getContributedReferences(this)
    }

    /**
    *
    */
    override fun getReference(): GlslVariableReference? {
        return references.firstOrNull() as? GlslVariableReference
    }

    /**
    *
    */
    override fun getName(): String {
        return node.text.replace("IntellijIdeaRulezzz", "")
    }

    /**
    *
    */
    override fun replaceElementName(newName: String?): GlslIdentifier? {
        if (!GlslUtils.isShaderFile(this)) return this
        if (newName == null ) return this
        val dummyDeclaration = "void $newName;"
        val dummyElement = (PsiFileFactory.getInstance(project)
            .createFileFromText("dummy.glsl", GlslFileType(), dummyDeclaration) as GlslFile)
            .firstChild
        val newIdentifierNode = PsiTreeUtil.findChildOfType(dummyElement, GlslVariable::class.java) ?: return this
        return replace(newIdentifierNode) as? GlslIdentifier
    }

    /**
     *
     */
    fun isEmpty(): Boolean {
        return node.text == "IntellijIdeaRulezzz"
    }
}