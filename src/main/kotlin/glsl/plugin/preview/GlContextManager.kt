package glsl.plugin.preview

import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessListener
import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import fleet.util.computeIfAbsentShim
import glsl.plugin.preview.run.GLProcessHandler
import glsl.plugin.preview.run.ShaderProgramCompiler
import glsl.plugin.preview.run.settings.FragShaderRunOptions
import glsl.plugin.preview.run.settings.UniformType
import glsl.plugin.utils.exceptions.ShaderCompilerException
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.awt.AWTGLCanvas
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent

/**
 * Manages rendering, uniforms and compilation of shader programs.
 * It also manages the GlContext.
 *
 * To compile and run a shader program, call [queueCompile].
 *
 */
class GlContextManager : Disposable {

    private var glCanvas: AWTGLCanvas;
    private val project: Project;


    // OpenGL resources
    private var initialized = false
    private var startNs = 0L
    private var programId: Int = -1

    // uniforms (optional)
    private var uTimeLocation = -1
    private var uResolutionLocation = -1
    private var uMouseLocation = -1

    // rendering variables
    private var positionLocation = -1
    private var positionBuffer = -1
    private var vertexArrayBuffer: Int = 0


    private var pendingCompile: CompileRun? = null
    private var currentRunning: GLProcessHandler? = null
    private var pendingStop: Boolean = false //true if the panel should stop rendering in the next frame.

    /** Queueable compile task */
    private data class CompileRun(val settings: FragShaderRunOptions, val processHandler: GLProcessHandler);

    val processTerminatedListener = object : ProcessListener {
        override fun processTerminated(processEvent: ProcessEvent) =
            this@GlContextManager.onProcessTerminated(processEvent)
    }


    companion object {
        private val LOG = Logger.getInstance(GlContextManager::class.java)

        @Volatile
        private var instances: MutableMap<Project, GlContextManager> = HashMap()

        fun getInstance(project: Project): GlContextManager {
            return instances[project] ?: synchronized(this) {
                instances.computeIfAbsentShim(project, ::GlContextManager)
            }
        }
    }

    private constructor(project: Project) {
        this.project = project;
    }

    /**
     * Initialize the GL context and paint the canvas.
     */
    init {
        this.glCanvas = object : AWTGLCanvas() {

            override fun addNotify() {
                super.addNotify()
                LOG.debug("AWTGLCanvas addNotify: displayable=$isDisplayable showing=$isShowing size=$size")
            }

            override fun initGL() {
                GL.createCapabilities()
                initialized = true
                startNs = System.nanoTime()

                glClearColor(0.5f, 0.1f, 0.5f, 1f)
                LOG.debug("GL initialized.")

            }

            /**
             * Renders a frame. Looks for pending stop and compile tasks.
             */
            override fun paintGL() {
                if (pendingStop) {
                    glDeleteProgram(programId)
                    programId = -1
                    clearCanvas()
                    pendingStop = false
                    currentRunning = null
                    return
                }
                if (pendingCompile != null) {
                    pendingCompile!!.processHandler.addProcessListener(processTerminatedListener)
                    compile(pendingCompile!!)
                    currentRunning = pendingCompile!!.processHandler
                    pendingCompile = null
                }
                if (programId != -1) {
                    this@GlContextManager.render()
                }
                swapBuffers()//need to call that always because otherwise the canvas would not react to resize
            }

            /**
             * Empty front and back buffers.
             */
            fun clearCanvas() {
                for (i in 0 until 2) {
                    glClearColor(0f, 0f, 0f, 1f)
                    glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT or GL_STENCIL_BUFFER_BIT)
                    if (positionBuffer != 0) {
                        glDeleteBuffers(positionBuffer)
                        positionBuffer = 0
                    }
                    swapBuffers()
                }
            }

        }

        glCanvas.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent?) {
                LOG.debug("AWTGLCanvas resized: size=${glCanvas.size}, bounds=${glCanvas.bounds}")
            }

            override fun componentShown(e: ComponentEvent?) {
                LOG.debug("AWTGLCanvas shown: size=${glCanvas.size}, bounds=${glCanvas.bounds}")
            }
        })
    }

    /**
     * Deletes the current shader program and clears the canvas.
     */
    private fun onProcessTerminated(processEvent: ProcessEvent) {
        (processEvent.processHandler as GLProcessHandler).printStdout("Process terminated");
        pendingStop = true;
    }

    /**
     * Add a compile request to the queue. All glsl logs will be printed to the process handler.
     * The request will be handled with the next render cycle.
     */
    fun queueCompile(runOptions: FragShaderRunOptions, processHandler: GLProcessHandler) {
        if (this.currentRunning != null) {
            JBPopupFactory.getInstance().createConfirmation(
                "Cancel current shader program?",
                "Yes", "No",
                {
                    this.currentRunning!!.printStdout("Stopping current shader program... (Triggered by user)")
                    this.currentRunning!!.terminate(200)
                    this.currentRunning = null
                    this.pendingCompile = CompileRun(runOptions, processHandler)
                },
                {
                    processHandler.terminate(200)
                },
                0
            ).showCenteredInCurrentWindow(project)
        } else {
            pendingCompile = CompileRun(runOptions, processHandler)
        }
    }

    private fun compile(compileRun: CompileRun) {
        try {
            LOG.debug("Compiling shader program:")
            val shaderProgramCompiler = ShaderProgramCompiler(compileRun.processHandler);
            this.programId = shaderProgramCompiler.getProgramFromFrag(compileRun.settings.getFragDocument().text)
            setupRenderContext(compileRun.settings.getUniformMappings())
            runShaderProgram()
        } catch (e: Exception) {
            if (e is ShaderCompilerException) {
                compileRun.processHandler.printStderr(e.shaderInfoLog)
                compileRun.processHandler.terminate(69)//this exit code means nothing
            } else {
                throw e;
            }
            this.programId = -1
        }
    }

    /**
     * Run shader program if there is one in the gl context.
     */
    private fun runShaderProgram() {
        if (!initialized) {
            throw IllegalStateException("GL not initialized")
        }
        if (programId == -1) {
            throw IllegalStateException("Program not compiled")
        }
        startNs = System.nanoTime()
        glUseProgram(programId)
        glCanvas.requestFocus()
        glCanvas.setVisible(true)
    }

    /**
     * @return the shader canvas
     */
    fun getCanvas(): AWTGLCanvas {
        return this.glCanvas;
    }


    override fun dispose() {
        if (!initialized) return

        if (programId != 0) glDeleteProgram(programId)
        if (vertexArrayBuffer != 0) glDeleteBuffers(vertexArrayBuffer)

        glCanvas.disposeCanvas();
    }


    private fun setupRenderContext(uniformMapping: Map<UniformType, String>) {
        LOG.debug("Setup render context:")
        positionBuffer = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, positionBuffer)
        // Fullscreen triangle in NDC:
        val positions = floatArrayOf(
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            -1f, 1f,
            1f, -1f,
            1f, 1f
        )

        glBufferData(GL_ARRAY_BUFFER, positions, GL_STATIC_DRAW)

        //todo make layout feature possible

        positionLocation = glGetAttribLocation(programId, "position")
        uTimeLocation =
            glGetUniformLocation(programId, uniformMapping.getOrDefault(UniformType.TIME, UniformType.TIME.defaultName))
        uResolutionLocation = glGetUniformLocation(
            programId,
            uniformMapping.getOrDefault(UniformType.RESOLUTION, UniformType.RESOLUTION.defaultName)
        )
        uMouseLocation = glGetUniformLocation(
            programId,
            uniformMapping.getOrDefault(UniformType.MOUSE, UniformType.MOUSE.defaultName)
        )

    }

    /**
     * Render GL context stuff and update uniforms.
     */
    private fun render() {
        val w = (glCanvas.width.coerceAtLeast(1) * glCanvas.graphicsConfiguration.defaultTransform.scaleX).toInt()
        val h = (glCanvas.height.coerceAtLeast(1) * glCanvas.graphicsConfiguration.defaultTransform.scaleY).toInt()


        glViewport(0, 0, w, h)
        glClearColor(0f, 0f, 0f, 1f)
        glClear(GL_COLOR_BUFFER_BIT)

        glUseProgram(programId)
        glEnableVertexAttribArray(positionLocation)
        glBindBuffer(GL_ARRAY_BUFFER, positionBuffer)
        glVertexAttribPointer(positionLocation, 2, GL_FLOAT, false, 0, 0)

        val time = (System.nanoTime() - startNs) / 1_000_000_000.0f //time in seconds
        glUniform1f(uTimeLocation, time)
        glUniform2f(uResolutionLocation, w.toFloat(), h.toFloat())
        val mousePos = glCanvas.mousePosition;
        if (mousePos == null) {
            glUniform2f(uMouseLocation, -1f, -1f)
        } else {
            glUniform2f(uMouseLocation, mousePos.x.toFloat(), mousePos.y.toFloat())
        }

        glDrawArrays(GL_TRIANGLES, 0, 6)
    }
}