package glsl.plugin.utils

import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.TemplateContextType
import glsl.plugin.language.GlslFile

class GlslTemplateContext : TemplateContextType( "GLSL") {
    override fun isInContext(templateActionContext: TemplateActionContext): Boolean {
        return templateActionContext.file is GlslFile
    }
}