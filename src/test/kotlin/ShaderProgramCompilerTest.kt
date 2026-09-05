import com.intellij.testFramework.fixtures.BasePlatformTestCase
import glsl.plugin.preview.run.GLProcessHandler
import glsl.plugin.preview.run.ShaderProgramCompiler
import glsl.plugin.utils.exceptions.ShaderCompilerException
import org.lwjgl.glfw.GLFW.GLFW_FALSE
import org.lwjgl.glfw.GLFW.GLFW_VISIBLE
import org.lwjgl.glfw.GLFW.glfwCreateWindow
import org.lwjgl.glfw.GLFW.glfwDestroyWindow
import org.lwjgl.glfw.GLFW.glfwInit
import org.lwjgl.glfw.GLFW.glfwMakeContextCurrent
import org.lwjgl.glfw.GLFW.glfwTerminate
import org.lwjgl.glfw.GLFW.glfwWindowHint
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL20.glIsProgram
import org.lwjgl.system.MemoryUtil.NULL

/**
 * Exercises [ShaderProgramCompiler] against a real OpenGL context. The context is created via a
 * hidden GLFW window rather than the plugin's own AWTGLCanvas, since that needs a realized Swing
 * component and isn't practical to drive from a unit test - GLFW gives the same kind of
 * (default/compatibility-profile) context without a visible UI.
 *
 * On a machine without a GPU (e.g. CI), this needs a software OpenGL driver - see
 * .github/workflows/pr.yml.
 */
class ShaderProgramCompilerTest : BasePlatformTestCase() {

    private var window = NULL

    override fun setUp() {
        super.setUp()
        check(glfwInit()) { "Unable to initialize GLFW" }
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        window = glfwCreateWindow(1, 1, "shader-compiler-test", NULL, NULL)
        check(window != NULL) { "Unable to create a headless GLFW window/GL context" }
        glfwMakeContextCurrent(window)
        GL.createCapabilities()
    }

    override fun tearDown() {
        try {
            if (window != NULL) {
                glfwDestroyWindow(window)
                window = NULL
            }
            glfwTerminate()
        } finally {
            super.tearDown()
        }
    }

    fun testValidFragmentShaderCompilesAndLinksAProgram() {
        val compiler = ShaderProgramCompiler(GLProcessHandler())

        val programId = compiler.getProgramFromFrag(
            """
            void main() {
                gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
            }
            """.trimIndent()
        )

        assertTrue("expected a valid (non-zero) program id", programId != 0)
    }

    fun testInvalidFragmentShaderThrowsWithTheCompilerLog() {
        val compiler = ShaderProgramCompiler(GLProcessHandler())

        try {
            compiler.getProgramFromFrag("this is not glsl at all;")
            fail("expected a ShaderCompilerException")
        } catch (e: ShaderCompilerException) {
            assertFalse("expected a non-empty shader info log", e.shaderInfoLog.isBlank())
        }
    }

    fun testRecompilingStillProducesAValidProgram() {
        val compiler = ShaderProgramCompiler(GLProcessHandler())
        val firstProgramId = compiler.getProgramFromFrag("void main() { gl_FragColor = vec4(1.0); }")
        assertTrue("expected a valid program after the first compile", glIsProgram(firstProgramId))

        val secondProgramId = compiler.getProgramFromFrag("void main() { gl_FragColor = vec4(0.0); }")

        assertTrue("expected a valid program after recompiling", glIsProgram(secondProgramId))
    }
}
