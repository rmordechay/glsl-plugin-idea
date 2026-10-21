import com.intellij.execution.configurations.RuntimeConfigurationError
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import glsl.plugin.preview.run.settings.ShaderRunConfiguration
import glsl.plugin.preview.run.settings.ShaderRunConfigurationFactory
import glsl.plugin.preview.run.settings.ShaderRunConfigurationType
import glsl.plugin.preview.run.settings.UniformType

/**
 * Covers [ShaderRunConfiguration]'s validation and uniform-mapping delegation - the parts of the
 * shader preview/run feature that don't need a live OpenGL context.
 */
class ShaderRunConfigurationTest : BasePlatformTestCase() {

    private fun newConfiguration(): ShaderRunConfiguration {
        val factory = ShaderRunConfigurationFactory(ShaderRunConfigurationType())
        return ShaderRunConfiguration(project, factory, "Fragment Shader")
    }

    fun testCheckConfigurationPassesForAnExistingFragmentFile() {
        val file = myFixture.configureByText("preview.frag", "void main() {}").virtualFile
        val configuration = newConfiguration()
        configuration.setFragmentFile(file.url)

        configuration.checkConfiguration()
    }

    fun testChecksRejectAMissingFragmentFileWithoutForgettingIt() {
        val configuration = newConfiguration()
        configuration.setFragmentFile("file://this/file/does/not/exist.frag")

        val checks = mapOf<String, () -> Unit>(
            "checkConfiguration" to configuration::checkConfiguration,
            "checkSettingsBeforeRun" to configuration::checkSettingsBeforeRun,
        )
        for ((name, check) in checks) {
            try {
                check()
                fail("expected $name to reject a fragment file that doesn't exist")
            } catch (e: RuntimeConfigurationError) {
                // ConfigurationException.getMessage() is deprecated in favor of the HTML message.
                val message = e.messageHtml.toString()
                assertTrue(
                    "expected $name's error to name the missing file, got: $message",
                    message.contains("this/file/does/not/exist.frag")
                )
            }
            assertEquals(
                "expected $name to keep the configured path, so the configuration works again once the file is back",
                "file://this/file/does/not/exist.frag",
                configuration.getFragmentFile()
            )
        }
    }

    fun testCheckConfigurationPassesAgainOnceAMissingFragmentFileIsBack() {
        val configuration = newConfiguration()
        configuration.setFragmentFile("temp:///src/later.frag")
        try {
            configuration.checkConfiguration()
            fail("expected checkConfiguration to reject the fragment file before it exists")
        } catch (_: RuntimeConfigurationError) {
        }

        myFixture.addFileToProject("later.frag", "void main() {}")

        // Throws (and fails the test) if the configuration still considers the file missing.
        configuration.checkConfiguration()
    }

    fun testSetUniformNameMergesIntoExistingMappings() {
        val configuration = newConfiguration()
        configuration.setUniformName(UniformType.TIME, "uMyTime")

        configuration.setUniformName(UniformType.MOUSE, "uMyMouse")

        assertEquals(
            "expected the new uniform name to be merged into the existing mappings",
            mapOf(UniformType.TIME to "uMyTime", UniformType.MOUSE to "uMyMouse"),
            configuration.getUniforms()
        )
    }

    fun testSetUniformNamesReplacesAllExistingMappings() {
        val configuration = newConfiguration()
        configuration.setUniformName(UniformType.TIME, "uMyTime")

        configuration.setUniformNames(mapOf(UniformType.MOUSE to "uMyMouse"))

        assertEquals(
            "expected setUniformNames to replace all existing mappings",
            mapOf(UniformType.MOUSE to "uMyMouse"),
            configuration.getUniforms()
        )
    }
}
