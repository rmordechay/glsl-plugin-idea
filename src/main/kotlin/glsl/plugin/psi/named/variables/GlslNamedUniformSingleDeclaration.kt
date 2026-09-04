package glsl.plugin.psi.named.variables

import com.intellij.lang.ASTNode
import glsl.plugin.psi.named.GlslNamedVariable

interface GlslUniformPsiElement : GlslNamedVariable


abstract class GlslNamedUniformSingleDeclaration(node: ASTNode) :
    GlslNamedSingleDeclaration(node),
    GlslUniformPsiElement