package glsl.plugin.language

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.FileViewProvider
import javax.swing.Icon


/**
 *
 */
class GlslFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, GlslLanguage.INSTANCE) {
    /**
    *
    */
    override fun toString(): String = "Glsl File"

    /**
    *
    */
    override fun getFileType(): FileType = GlslFileType()
}

/**
 *
 */
object GlslIcon {
    val PLUGIN_FILE_ICON = IconLoader.getIcon("icons/file-icon.svg", GlslIcon::class.java)
}

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