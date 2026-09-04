package glsl.plugin.preview.run.settings

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.NonNls

class ShaderRunConfigurationFactory(type: ShaderRunConfigurationType) : ConfigurationFactory(type) {

    override fun createTemplateConfiguration(project: Project): RunConfiguration {
        return ShaderRunConfiguration(project, this, name = "Fragment Shader")
    }

    override fun getOptionsClass(): Class<out FragShaderRunOptions> {
        return FragShaderRunOptions::class.java
    }

    override fun getId(): @NonNls String {
        return "ShaderRunConfigurationFactory"
    }
}