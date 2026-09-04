package glsl.plugin.preview.run

import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.filters.UrlFilter
import com.intellij.execution.process.NopProcessHandler
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import glsl.plugin.preview.GlContextManager
import glsl.plugin.preview.run.settings.FragShaderRunOptions

class FragmentShaderRunProfileState(private val project: Project, private val options: FragShaderRunOptions) :
    RunProfileState {

    val glContextManager = GlContextManager.getInstance(project)

    override fun execute(
        executor: Executor?,
        runner: ProgramRunner<*>
    ): ExecutionResult? {
        val processHandler: GLProcessHandler = GLProcessHandler()
        val consoleView: ConsoleView = TextConsoleBuilderFactory.getInstance()
            .createBuilder(project)
            .filters(UrlFilter(project))
            .console

        consoleView.attachToProcess(processHandler)
        processHandler.startNotify()

        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow("GLSL Preview")

        if (toolWindow == null) {
            consoleView.print("Could not find tool window 'GLSL Preview'.\n", ConsoleViewContentType.ERROR_OUTPUT)
            return DefaultExecutionResult(consoleView, NopProcessHandler())
        }
        toolWindow.show();

        glContextManager.queueCompile(options, processHandler); //todo maybe implement some kind of already baker compiler settings
        return DefaultExecutionResult(consoleView, processHandler);
    }
}
