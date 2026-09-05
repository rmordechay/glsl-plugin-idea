package glsl.plugin.preview.run.settings

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.intellij.execution.configurations.LocatableRunConfigurationOptions
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.vfs.VirtualFileManager
import java.util.Objects

/** Set only if document for fragment shader source could not be found and you want to mark this as an error */
const val EMPTY_FILE_INPUT = "EMPTY"

/**
 * Data class for run - options of fragment shaders
 */
class FragShaderRunOptions() : LocatableRunConfigurationOptions() {
    var fragmentFile: String? by string()
    var vertexFile: String? by string()//todo not yet implemnented
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
        val newMap = HashMap(getUniformMappings());
        newMap[uniform] = name
        uniformMappingsRaw =
            jacksonObjectMapper().writeValueAsString(newMap) //I know this is inefficient AF but JetBrains generates this class anyway every 2 seconds new for some reason
    }

    /**
     * Get fragment shader source document
     */
    fun getFragDocument(): Document {
        return runReadAction {
            Objects.requireNonNull(fragmentFile)
            val fragFile = Objects.requireNonNull(
                VirtualFileManager.getInstance().findFileByUrl(fragmentFile!!),
                "Could not find file for $fragmentFile"
            )!!
            Objects.requireNonNull(FileDocumentManager.getInstance().getDocument(fragFile),"Could not find document for $fragmentFile")!!
        }
    }


}







