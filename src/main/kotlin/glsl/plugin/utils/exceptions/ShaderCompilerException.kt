package glsl.plugin.utils.exceptions

/**
 * Exception to throw when glsl shader compilation fails.
 * @param shaderInfoLog the shader info log which contains the cause
 */
class ShaderCompilerException(val shaderInfoLog: String) : RuntimeException(shaderInfoLog) {
}