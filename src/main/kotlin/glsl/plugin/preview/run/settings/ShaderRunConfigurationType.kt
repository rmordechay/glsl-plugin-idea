package glsl.plugin.preview.run.settings

import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.openapi.util.IconLoader

/**
 * Implements [com.intellij.execution.configurations.ConfigurationType]. Wraps [com.intellij.execution.configurations.RunProfile]
 */
class ShaderRunConfigurationType : ConfigurationTypeBase(
    id = "FragmentShaderRunConfigurationType",
    displayName = "Fragment Shader",
    description = "Run a fragment shader",
    icon = IconLoader.getIcon("/icons/file-icon.svg", ShaderRunConfigurationType::class.java)
) {
    init {
        addFactory(ShaderRunConfigurationFactory(this))
    }
}