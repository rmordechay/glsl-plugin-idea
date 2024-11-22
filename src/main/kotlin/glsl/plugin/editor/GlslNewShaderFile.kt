package glsl.plugin.editor

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import glsl.plugin.language.GlslIcon

private const val NEW_SHADER_DIALOG_TEXT = "Shader File"

class GlslNewShaderFile
    : CreateFileFromTemplateAction(NEW_SHADER_DIALOG_TEXT, "", GlslIcon.PLUGIN_FILE_ICON), DumbAware {

    /**
    *
    */
    override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
        builder.setTitle(NEW_SHADER_DIALOG_TEXT)
            .addKind("Shader (.glsl)", GlslIcon.PLUGIN_FILE_ICON, "shader-template")
            .addKind("Vertex (.vert)", GlslIcon.PLUGIN_FILE_ICON, "vs-template")
            .addKind("Fragment (.frag)", GlslIcon.PLUGIN_FILE_ICON, "fs-template")
            .addKind("Geometry (.geom)", GlslIcon.PLUGIN_FILE_ICON, "gs-template")
            .addKind("Tessellation-Evaluation (.tese)", GlslIcon.PLUGIN_FILE_ICON, "te-template")
            .addKind("Tessellation-Control (.tesc)", GlslIcon.PLUGIN_FILE_ICON, "tc-template")
            .addKind("Compute (.comp)", GlslIcon.PLUGIN_FILE_ICON, "cs-template")
    }

    /**
    *
    */
    override fun getActionName(directory: PsiDirectory?, newName: String, templateName: String?): String {
        return NEW_SHADER_DIALOG_TEXT
    }

    /**
    *
    */
    override fun hashCode(): Int {
        return 0
    }

    /**
    *
    */
    override fun equals(other: Any?): Boolean {
        return other is GlslNewShaderFile
    }
}