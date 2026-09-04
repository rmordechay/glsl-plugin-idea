package glsl.plugin.preview.run.settings

/**
 * Enum class for uniform types used in fragment shaders.
 *
 * Will maybe add a CUSTOM type later
 */
enum class UniformType(val label: String,val defaultName: String, val tooltip: String = "") {
    TIME("Time", "uTime", "Elapsed time in seconds since the shader program started as <code>float</code>"),
    RESOLUTION("Resolution", "uResolution", "Resolution of the canvas as <code>vec2</code>"),
    MOUSE("Mouse", "uMouse", "Mouse position as <code>vec2</code><p><b>Hint</b><br> Y position is inverted. Origin is top left. If mouse is outside canvas mouse position will always be [-1,-1]</p>"),
}