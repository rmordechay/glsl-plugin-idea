package glsl.plugin.preview.run.gutteraction

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.LazyRunConfigurationProducer
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import glsl.plugin.preview.run.settings.ShaderRunConfiguration
import glsl.plugin.preview.run.settings.ShaderRunConfigurationFactory
import glsl.plugin.preview.run.settings.ShaderRunConfigurationType
import glsl.plugin.preview.run.settings.modifyOptionsFromShaderSourceFile

class FragShaderLazyRunConfigProducer : LazyRunConfigurationProducer<ShaderRunConfiguration>() {

    override fun getConfigurationFactory(): ConfigurationFactory {
        return ShaderRunConfigurationFactory(ShaderRunConfigurationType())
    }

    override fun setupConfigurationFromContext(
        configuration: ShaderRunConfiguration,
        context: ConfigurationContext,
        sourceElement: Ref<PsiElement?>
    ): Boolean {

        val location = context.location ?: return false;
        if (location.virtualFile?.name?.endsWith(".frag") == true || location.virtualFile?.name?.endsWith(".glsl") == true) {
            modifyOptionsFromShaderSourceFile(configuration, sourceElement.get()!!.containingFile!!)
            return true
        }
        return false

    }

    override fun isConfigurationFromContext(
        configuration: ShaderRunConfiguration,
        context: ConfigurationContext
    ): Boolean {
        return configuration.getFragmentFile() == context.psiLocation?.containingFile?.virtualFile?.path
    }
}