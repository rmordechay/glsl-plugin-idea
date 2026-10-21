package glsl.plugin.preview.run.settings

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.intellij.execution.configurations.LocatableRunConfigurationOptions
import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.vfs.VirtualFileManager

/**
 * Data class for run - options of fragment shaders
 */
class FragShaderRunOptions() : LocatableRunConfigurationOptions() {
    var fragmentFile: String? by string()
    var vertexFile: String? by string() //todo not yet implemented
    var uniformMappingsRaw: String? by string()

    /**
     * Get uniform mappings
     * @return Map<UniformType, String> Uniform type and the according name
     */
    fun getUniformMappings(): Map<UniformType, String> {
        return jacksonObjectMapper().readValue<Map<UniformType, String>>(uniformMappingsRaw ?: "{}")
    }

    fun setUniformMappings(uniformMappings: Map<UniformType, String>) {
        uniformMappingsRaw = jacksonObjectMapper().writeValueAsString(uniformMappings)
    }

    /**
     * Set name mapping for uniform
     * @param uniform UniformType
     * @param name String
     */
    fun setUniformName(uniform: UniformType, name: String) {
        val newMap = HashMap(getUniformMappings())
        newMap[uniform] = name
        uniformMappingsRaw =
            jacksonObjectMapper().writeValueAsString(newMap) //I know this is inefficient AF but JetBrains generates this class anyway every 2 seconds new for some reason
    }

    /**
     * Returns the document of the configured [fragmentFile], i.e. the fragment shader source the preview compiles.
     * The document reflects unsaved editor changes. Runs in a read action, so it can be called from any thread.
     *
     * @throws IllegalStateException if no fragment file is configured, or it (or its document) can't be found -
     * e.g. because the file was deleted after the run configuration was created.
     */
    fun getFragDocument(): Document {
        return runReadActionBlocking {
            val url = checkNotNull(fragmentFile) { "No fragment shader file configured" }
            val file = checkNotNull(VirtualFileManager.getInstance().findFileByUrl(url)) { "Could not find file for $url" }
            return@runReadActionBlocking checkNotNull(FileDocumentManager.getInstance().getDocument(file)) {
                "Could not find document for $url"
            }
        }
    }


}







