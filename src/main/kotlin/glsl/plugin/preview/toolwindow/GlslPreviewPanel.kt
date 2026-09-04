package glsl.plugin.preview.toolwindow

import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import glsl.plugin.preview.GlContextManager
import glsl.plugin.utils.swing.RelativeConstraints
import glsl.plugin.utils.swing.RelativeLayout
import java.awt.Canvas
import java.awt.Color
import java.awt.Graphics
import javax.swing.JPanel
import javax.swing.Timer

/**
 * Glsl preview - Tool Window
 */
class GlslPreviewPanel : Disposable {

    private val contentPanel: JPanel;
    private val project: Project;
    private val manager: GlContextManager;
    private val renderTimer: Timer;

    /**
     * Build canvas and init render timer.
     */
    constructor(project: Project) {
        this.project = project;
        this.manager = GlContextManager.getInstance(project);


        val emptyCanvas: Canvas = object : Canvas() {
            override fun paint(g: Graphics?) {
                g?.color = Color.RED;
                g?.fillRect(0, 0, getWidth(), getHeight())
            }
        }
        emptyCanvas.background = Color.BLUE

        val shaderCanvas = manager.getCanvas()

        val wrapperLayout = RelativeLayout();
        val canvasConstraints = RelativeConstraints()
            .centerInParent()
            .setWidth(RelativeConstraints.MATCH_PARENT)
            .setHeight(300)
            .setMarginLeft(10)
            .setMarginRight(10)
            .setAspectRatio(1.0)
        val canvasWrapper: JPanel = JPanel(wrapperLayout)

        canvasWrapper.add(shaderCanvas, canvasConstraints)
        contentPanel = canvasWrapper

        this.renderTimer = Timer(1000 / 60) {
            if (shaderCanvas.isDisplayable && shaderCanvas.width > 0 && shaderCanvas.height > 0) {
                shaderCanvas.render()
            }
        }
        renderTimer.start()

        //todo add uniform inspector
    }

    fun getPanel(): JPanel {
        return contentPanel
    }

    override fun dispose() {
        renderTimer.stop()
        manager.dispose()
    }
}