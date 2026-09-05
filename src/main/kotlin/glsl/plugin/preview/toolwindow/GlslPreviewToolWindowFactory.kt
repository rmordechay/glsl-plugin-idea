package glsl.plugin.preview.toolwindow

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory

/**
 * This class triggers deprecation warnings because of the parent ToolWindowFactory
 * default methods being/using deprecated and experimental methods.
 * There doesn't seem to be anything we can do about it at this point.
 */
class GlslPreviewToolWindowFactory : ToolWindowFactory, DumbAware {


    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentManager = toolWindow.contentManager
        val panel = GlslPreviewPanel(project)

        val content = contentManager.factory.createContent(panel.getPanel(), "", false)
        contentManager.addContent(content)
        Disposer.register(content, panel)

    }
}