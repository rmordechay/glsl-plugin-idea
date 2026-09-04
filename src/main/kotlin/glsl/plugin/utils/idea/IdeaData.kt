package glsl.plugin.utils.idea

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

/**
 * File Data
 */
data class FocusFileInfo(
    val virtualFile: VirtualFile,
    val name: String,
    val path: String,
    val text: String
)

fun getFocusedFileInfo(project: Project): FocusFileInfo? {
    val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return null
    val document = editor.document
    val vFile = FileDocumentManager.getInstance().getFile(document) ?: return null

    return FocusFileInfo(
        virtualFile = vFile,
        name = vFile.name,
        path = vFile.path,
        text = document.text
    )
}