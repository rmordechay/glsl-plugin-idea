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


class ShaderProgramCompiler {

    private var vertexShaderId = -1;
    private var programId = -1;
    private var fragShaderId = -1;
    private val processHandler: GLProcessHandler;

    constructor(processHandler: GLProcessHandler) {
        this.processHandler = processHandler
    }

    /**
     * Creates a program from a default vertex shader and a fragment shader.
     * @param fragShaderSource
     * @return program ID
     */
    fun getProgramFromFrag(fragShaderSource: String): Int {
        if (vertexShaderId == -1) {
            vertexShaderId = createVShader();
        }
        if (programId != -1) {
            glDetachShader(programId, vertexShaderId)
            glDeleteShader(fragShaderId)
            glDeleteProgram(programId)
        }
        compileFragShader(fragShaderSource)
        return compileProgram()
    }

    /**
     * Compile fragment shader.
     * @param shader source code of fragment shader
     * @return fragment shader ID
     */
    private fun compileFragShader(shader: String): Int {
        this.processHandler.printStdout("Compiling fragment shader...")
        val fragShaderId = compileShader(GL_FRAGMENT_SHADER, shader)
        this.fragShaderId = fragShaderId;
        return fragShaderId
    }

    /**
     * Compile program from vertex shader and fragment shader.
     * @return program ID
     */
    private fun compileProgram(): Int {
        LOG.debug("Compiling program:")
        if (vertexShaderId == -1 || fragShaderId == -1) throw IllegalStateException("Missing shaders")
        val programId = glCreateProgram()
        glAttachShader(programId, vertexShaderId)
        glAttachShader(programId, fragShaderId)
        glLinkProgram(programId)
        val infoLog = (glGetProgramInfoLog(programId))
        if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
            throw ShaderCompilerException(infoLog)
        } else {
            if(infoLog.trim().isNotEmpty()) {
                this.processHandler.printStdout(infoLog);
            }
        }
        this.programId = programId;
        return programId
    }

    /**
     * Compiles a shader.
     * @param shaderType the type of the shader ([GL_VERTEX_SHADER], [GL_FRAGMENT_SHADER])
     * @param shaderSource the source code of the shader
     * @return the ID of the compiled shader
     */
    private fun compileShader(shaderType: Int, shaderSource: String): Int {
        val shaderId = glCreateShader(shaderType)
        val shaderTypeStr = if (shaderType == GL_VERTEX_SHADER) "Vertex" else "Fragment"
        glShaderSource(shaderId, shaderSource)
        glCompileShader(shaderId)

        val infoLog = glGetShaderInfoLog(shaderId)
        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            throw ShaderCompilerException(infoLog)
        }
        if(infoLog.trim().isNotEmpty()) {
            processHandler.printStdout(infoLog);
        }
        processHandler.printStdout("$shaderTypeStr Shader compiled successfully");
        return shaderId
    }

    /**
     * Creates a vertex shader.
     * @return the ID of the created shader
     */
    private fun createVShader(): Int {
        processHandler.printStdout("Compiling vertex shader...")
        return compileShader(GL_VERTEX_SHADER, DEFAULT_VERTEX_SHADER_SOURCE)
    }

}