package glsl.plugin.language

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.FileViewProvider


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

