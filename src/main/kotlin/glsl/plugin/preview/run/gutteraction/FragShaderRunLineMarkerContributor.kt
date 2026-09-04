package glsl.plugin.preview.run.gutteraction

import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import glsl.plugin.preview.run.FragmentShaderRunAction
import glsl.plugin.utils.idea.isInFragFile
import glsl.psi.interfaces.GlslFunctionDeclarator

class FragShaderRunLineMarkerContributor : RunLineMarkerContributor() {

    override fun getInfo(element: PsiElement): Info? {
        val isFragmentShader = isInFragFile(element)
        if (isFragmentShader &&
            element is LeafPsiElement &&
            element.text.contains("main")
            && element.parent.parent is GlslFunctionDeclarator
            && element.parent.parent.text.startsWith("void")) {
            return Info(AllIcons.Actions.Execute, arrayOf(FragmentShaderRunAction()))
        }
        return null;
    }
}