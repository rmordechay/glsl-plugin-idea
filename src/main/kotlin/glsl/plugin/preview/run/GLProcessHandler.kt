package glsl.plugin.preview.run

import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessOutputTypes
import java.io.OutputStream
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A special process handler that keeps the run content open until explicitly terminated.
 * You use it to react to events like manual user triggered termination. And you also use it to print to run console (for now)
 */
class GLProcessHandler : ProcessHandler() {

    private val terminated = AtomicBoolean(false)

    override fun destroyProcessImpl() {
        terminate(0)
    }

    override fun detachProcessImpl() {
        notifyProcessDetached()
    }

    override fun detachIsDefault(): Boolean = false

    override fun getProcessInput(): OutputStream? = null

    /**
     * Print normal plain text to the run console
     */
    fun printStdout(text: String) {
        if (terminated.get()) return
        notifyTextAvailable(ensureEndsWithNewline(text), ProcessOutputTypes.STDOUT)
    }

    /**
     * Print error text to the run console
     */
    fun printStderr(text: String) {
        if (terminated.get()) return
        notifyTextAvailable(ensureEndsWithNewline(text), ProcessOutputTypes.STDERR)
    }

    /**
     * Terminate the process with the given exit code
     * @param exitCode idk
     */
    fun terminate(exitCode: Int) {
        if (!terminated.compareAndSet(false, true)) return
        notifyProcessTerminated(exitCode)
    }

    private fun ensureEndsWithNewline(s: String): String =
        if (s.endsWith("\n")) s else "$s\n"
}