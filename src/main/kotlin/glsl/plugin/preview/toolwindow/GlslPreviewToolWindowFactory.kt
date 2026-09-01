package glsl.plugin.preview.toolwindow

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory

class GlslPreviewToolWindowFactory : ToolWindowFactory, DumbAware {


    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentManager = toolWindow.contentManager
        val panel = GlslPreviewPanel(project)

        val content = contentManager.factory.createContent(panel.getPanel(), "", false)
        contentManager.addContent(content)
        Disposer.register(content, panel)

    }
}