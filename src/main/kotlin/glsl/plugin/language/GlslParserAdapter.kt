package glsl.plugin.language

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.psi.tree.IElementType
import glsl._GlslParser

class GlslParserAdapter : _GlslParser() {
    override fun parse(root: IElementType, builder: PsiBuilder): ASTNode {
        return super.parse(root, builder)
    }
}
