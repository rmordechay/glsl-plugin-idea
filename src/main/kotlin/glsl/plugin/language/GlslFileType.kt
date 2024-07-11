package glsl.plugin.language

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

/**
 *
 */
class GlslFileType : LanguageFileType(GlslLanguage.INSTANCE) {

    /**
    *
    */
    override fun getName(): String = "Glsl File"

    /**
    *
    */
    override fun getDescription(): String = "OpenGL shading language file"

    /**
    *
    */
    override fun getDefaultExtension(): String {
        return "glsl"
    }

    /**
    *
    */
    override fun getIcon(): Icon = GlslIcon.PLUGIN_FILE_ICON
}