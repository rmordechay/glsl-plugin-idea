package glsl.plugin.preview.run.settings

import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
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
        return ShaderSettingsEditor()
    }

    fun setFragmentFile(path: String) {
        options.fragmentFile = path;
    }

    fun getFragmentFile(): String? {
        return options.fragmentFile;
    }

    fun setUniformName(uniform: UniformType, name: String) {
        options.setUniformName(uniform, name);
    }

    fun setUniformNames(uniforms: Map<UniformType, String>) {
        options.setUniformMappings(uniforms)
    }

    fun getUniforms(): Map<UniformType, String> {
        return options.getUniformMappings()
    }

    override fun checkSettingsBeforeRun() {
        try {
            options.getFragDocument()
        } catch (e: NullPointerException) {
            options.fragmentFile = EMPTY_FILE_INPUT
            throw RuntimeConfigurationError("Fragment file could not be found.")
        }
    }

    override fun checkConfiguration() {
        try {
            options.getFragDocument()
        } catch (e: NullPointerException) {
            options.fragmentFile = EMPTY_FILE_INPUT
            throw RuntimeConfigurationError("Fragment file could not be found.")
        }
    }
}