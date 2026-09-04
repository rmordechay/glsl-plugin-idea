package glsl.plugin.preview.run

import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.get
import glsl.plugin.preview.run.settings.ShaderRunConfiguration
import glsl.plugin.preview.run.settings.ShaderRunConfigurationFactory
import glsl.plugin.preview.run.settings.ShaderRunConfigurationType
import glsl.plugin.preview.run.settings.modifyOptionsFromShaderSourceFile

class FragmentShaderRunAction : AnAction() {


    override fun actionPerformed(e: AnActionEvent) {
        val runManager = RunManager.getInstance(e.project!!)
        val configFactory = ShaderRunConfigurationFactory(ShaderRunConfigurationType())
        val sourceFile = e.dataContext[CommonDataKeys.PSI_FILE]!!
        var runnerAndConfig = runManager.getConfigurationSettingsList(ShaderRunConfigurationType())
            .findLast { it ->
                (it.configuration as ShaderRunConfiguration).getFragmentFile() == sourceFile.virtualFile?.url
            }
        val isNew = runnerAndConfig == null
        if (isNew) {
            runnerAndConfig = runManager.createConfiguration(
                "Fragment Shader [${sourceFile.virtualFile.nameWithoutExtension}]",
                configFactory
            )
            modifyOptionsFromShaderSourceFile(runnerAndConfig.configuration as ShaderRunConfiguration, sourceFile)
            runManager.addConfiguration(runnerAndConfig)
        }
        runManager.selectedConfiguration = runnerAndConfig;
        ProgramRunnerUtil.executeConfiguration(runnerAndConfig, DefaultRunExecutor.getRunExecutorInstance())
    }
}