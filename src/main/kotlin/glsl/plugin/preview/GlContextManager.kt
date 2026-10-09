package glsl.plugin.preview

import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessListener
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
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
 * There is one instance per project (a project-level service), created on first use and disposed by the
 * platform when its project is closed - get it with [getInstance].
 */
@Service(Service.Level.PROJECT)
class GlContextManager(private val project: Project) : Disposable {

    private var glCanvas: AWTGLCanvas;


    // OpenGL resources. They all live in the canvas's GL context, and die with it.
    private var initialized = false
    private var startNs = 0L
    private var programId: Int = 0 // 0: no program (OpenGL never uses 0 as a program ID)

    // uniforms (optional)
    private var uTimeLocation = -1
    private var uResolutionLocation = -1
    private var uMouseLocation = -1

    // rendering variables
    private var positionLocation = -1
    private var positionBuffer = 0 // 0: no buffer (glGenBuffers never returns 0)


    private var pendingCompile: CompileRun? = null
    private var currentRun: CompileRun? = null
    private var pendingStop: Boolean = false //true if the panel should stop rendering in the next frame.

    /** Queueable compile task */
    private data class CompileRun(val settings: FragShaderRunOptions, val processHandler: GLProcessHandler);

    val processTerminatedListener = object : ProcessListener {
        override fun processTerminated(processEvent: ProcessEvent) =
            this@GlContextManager.onProcessTerminated(processEvent)
    }


    companion object {
        private val LOG = Logger.getInstance(GlContextManager::class.java)

        /**
         * Returns [project]'s manager, creating it on first use. Use this from anything that needs the
         * project's shader preview, e.g. to show its canvas or queue a shader for compilation. The manager is
         * owned by the project, so callers must not dispose it.
         */
        fun getInstance(project: Project): GlContextManager = project.service()
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

            /**
             * Removing the canvas from the UI (e.g. closing, floating or docking its tool window) makes
             * [AWTGLCanvas] destroy its GL context, and every program and buffer in it with it. Forget their IDs,
             * so they are never used against a different context - if the canvas is shown again, it gets a new
             * context and runs [initGL] again.
             *
             * If a shader is still running, it's queued to be compiled again, so it comes back as soon as the
             * canvas is shown again instead of leaving a black preview behind.
             */
            override fun removeNotify() {
                super.removeNotify()
                initialized = false
                programId = 0
                positionBuffer = 0
                LOG.debug("AWTGLCanvas removeNotify: GL context and its resources are gone")

                val run = currentRun
                if (run != null && pendingCompile == null) {
                    run.processHandler.printStdout("The preview lost its GL context (e.g. its window was moved), recompiling...")
                    pendingCompile = run
                }
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
                    programId = 0
                    clearCanvas()
                    pendingStop = false
                    currentRun = null
                    // A run that ended before its (re)compile came around must not be compiled anymore.
                    if (pendingCompile?.processHandler?.isProcessTerminated == true) {
                        pendingCompile = null
                    }
                    return
                }
                val compileRun = pendingCompile
                if (compileRun != null) {
                    pendingCompile = null
                    // A run that's re-queued after losing the GL context (see removeNotify) already has the listener.
                    if (compileRun !== currentRun) {
                        compileRun.processHandler.addProcessListener(processTerminatedListener)
                    }
                    compile(compileRun)
                    currentRun = compileRun
                }
                if (programId != 0) {
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
        val running = currentRun
        if (running != null) {
            JBPopupFactory.getInstance().createConfirmation(
                "Cancel current shader program?",
                "Yes", "No",
                {
                    running.processHandler.printStdout("Stopping current shader program... (Triggered by user)")
                    running.processHandler.terminate(200)
                    currentRun = null
                    pendingCompile = CompileRun(runOptions, processHandler)
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
            val shaderProgramCompiler = ShaderProgramCompiler(compileRun.processHandler)
            // The compiler hands ownership of the program to us, so the previous one is ours to delete.
            if (programId != 0) {
                glDeleteProgram(programId)
                programId = 0
            }
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
            this.programId = 0
        }
    }

    /**
     * Run shader program if there is one in the gl context.
     */
    private fun runShaderProgram() {
        if (!initialized) {
            throw IllegalStateException("GL not initialized")
        }
        if (programId == 0) {
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


    /**
     * Called by the platform when the project closes. Deletes the GL program and buffer if the canvas's context
     * still exists, making that context current first: with several projects open, another project's context
     * may be current, and its objects can have the same IDs as ours.
     *
     * If the canvas has already been removed from the UI, [AWTGLCanvas.removeNotify] has destroyed the context
     * (and the canvas) already, so there is nothing left to clean up. Calling [AWTGLCanvas.runInContext] then
     * would create a new, empty context instead.
     */
    override fun dispose() {
        if (!initialized || !glCanvas.isDisplayable) return

        glCanvas.runInContext {
            if (programId != 0) glDeleteProgram(programId)
            if (positionBuffer != 0) glDeleteBuffers(positionBuffer)
        }
        programId = 0
        positionBuffer = 0
        // The canvas itself (and its context) is disposed by AWTGLCanvas.removeNotify when it leaves the UI.
    }


    private fun setupRenderContext(uniformMapping: Map<UniformType, String>) {
        LOG.debug("Setup render context:")
        if (positionBuffer != 0) glDeleteBuffers(positionBuffer)
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