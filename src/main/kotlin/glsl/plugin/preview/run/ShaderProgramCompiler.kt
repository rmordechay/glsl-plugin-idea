package glsl.plugin.preview.run

import com.intellij.openapi.diagnostic.Logger
import glsl.plugin.utils.exceptions.ShaderCompilerException
import org.lwjgl.opengl.GL20.*


private const val DEFAULT_VERTEX_SHADER_SOURCE = """
                attribute vec2 position;
                void main() {
                    gl_Position = vec4(position, 0.0, 1.0);
                }
        """

private val LOG = Logger.getInstance(ShaderProgramCompiler::class.java)


/**
 * Compiles shader programs for the shader preview, printing compiler output to [processHandler]'s console.
 *
 * Doesn't hold any GL objects between calls: every program it returns belongs to the caller.
 */
class ShaderProgramCompiler(private val processHandler: GLProcessHandler) {

    /**
     * Compiles [fragShaderSource] together with the plugin's default full-screen vertex shader, and links them
     * into a new program. Used by the preview whenever a shader is (re)started.
     *
     * The caller owns the returned program and must delete it (`glDeleteProgram`) when it's no longer needed.
     * The intermediate shader objects are deleted before this returns, whether it succeeds or fails.
     *
     * Must be called with the GL context the program will be used in current.
     *
     * @throws ShaderCompilerException if compiling or linking fails; it carries the driver's info log.
     */
    fun getProgramFromFrag(fragShaderSource: String): Int {
        val vertexShaderId = createVertexShader()
        try {
            val fragShaderId = compileFragShader(fragShaderSource)
            try {
                return compileProgram(vertexShaderId, fragShaderId)
            } finally {
                glDeleteShader(fragShaderId)
            }
        } finally {
            glDeleteShader(vertexShaderId)
        }
    }

    /**
     * Compile fragment shader.
     * @param shader source code of fragment shader
     * @return fragment shader ID
     */
    private fun compileFragShader(shader: String): Int {
        processHandler.printStdout("Compiling fragment shader...")
        return compileShader(GL_FRAGMENT_SHADER, shader)
    }

    /**
     * Links [vertexShaderId] and [fragShaderId] into a new program and returns its ID. On success, both shaders
     * are detached again, so deleting them afterwards actually frees them. On failure, the program is deleted.
     *
     * @throws ShaderCompilerException if linking fails
     */
    private fun compileProgram(vertexShaderId: Int, fragShaderId: Int): Int {
        LOG.debug("Compiling program:")
        val programId = glCreateProgram()
        glAttachShader(programId, vertexShaderId)
        glAttachShader(programId, fragShaderId)
        glLinkProgram(programId)
        val infoLog = glGetProgramInfoLog(programId)
        if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
            glDeleteProgram(programId)
            throw ShaderCompilerException(infoLog)
        }
        // A linked program no longer needs its shaders, and a shader that's still attached isn't freed on delete.
        glDetachShader(programId, vertexShaderId)
        glDetachShader(programId, fragShaderId)
        if (infoLog.trim().isNotEmpty()) {
            processHandler.printStdout(infoLog)
        }
        return programId
    }

    /**
     * Compiles a shader. The shader object is deleted again if compilation fails.
     * @param shaderType the type of the shader ([GL_VERTEX_SHADER], [GL_FRAGMENT_SHADER])
     * @param shaderSource the source code of the shader
     * @return the ID of the compiled shader
     * @throws ShaderCompilerException if compilation fails
     */
    private fun compileShader(shaderType: Int, shaderSource: String): Int {
        val shaderId = glCreateShader(shaderType)
        val shaderTypeStr = if (shaderType == GL_VERTEX_SHADER) "Vertex" else "Fragment"
        glShaderSource(shaderId, shaderSource)
        glCompileShader(shaderId)

        val infoLog = glGetShaderInfoLog(shaderId)
        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            glDeleteShader(shaderId)
            throw ShaderCompilerException(infoLog)
        }
        if (infoLog.trim().isNotEmpty()) {
            processHandler.printStdout(infoLog)
        }
        processHandler.printStdout("$shaderTypeStr Shader compiled successfully")
        return shaderId
    }

    /**
     * Creates a vertex shader.
     * @return the ID of the created shader
     */
    private fun createVertexShader(): Int {
        processHandler.printStdout("Compiling vertex shader...")
        return compileShader(GL_VERTEX_SHADER, DEFAULT_VERTEX_SHADER_SOURCE)
    }

}
