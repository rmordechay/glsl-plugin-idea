package glsl.plugin.preview.run.settings

import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFileManager
import glsl.plugin.preview.run.FragmentShaderRunProfileState


/**
 * Implements [RunProfile]
 * Manages GlslRunSettings
 */
class ShaderRunConfiguration(
    project: Project,
    factory: ShaderRunConfigurationFactory,
    name: String?
) : LocatableConfigurationBase<FragShaderRunOptions>(project, factory, name) {


    override fun getOptions(): FragShaderRunOptions {
        return super.getOptions() as FragShaderRunOptions
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState {
        return FragmentShaderRunProfileState(project, options)
    }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        return ShaderSettingsEditor(project)
    }

    fun setFragmentFile(path: String) {
        options.fragmentFile = path
    }

    fun getFragmentFile(): String? {
        return options.fragmentFile
    }

    fun setUniformName(uniform: UniformType, name: String) {
        options.setUniformName(uniform, name)
    }

    fun setUniformNames(uniforms: Map<UniformType, String>) {
        options.setUniformMappings(uniforms)
    }

    fun getUniforms(): Map<UniformType, String> {
        return options.getUniformMappings()
    }

    override fun checkSettingsBeforeRun() = checkFragmentFileExists()

    override fun checkConfiguration() = checkFragmentFileExists()

    /**
     * Rejects a configuration whose fragment shader file is unset or doesn't exist. A [RuntimeConfigurationError]
     * keeps the configuration from running and shows the message in the run configuration dialog.
     *
     * The platform calls this often (whenever the dialog revalidates, and before every run), so it only checks:
     * the configured path is kept, and the configuration works again as soon as the file is back.
     */
    private fun checkFragmentFileExists() {
        val url = options.fragmentFile ?: throw RuntimeConfigurationError("No fragment shader file selected.")
        if (VirtualFileManager.getInstance().findFileByUrl(url) == null) {
            throw RuntimeConfigurationError("Fragment shader file could not be found: ${VfsUtilCore.urlToPath(url)}")
        }
    }
}