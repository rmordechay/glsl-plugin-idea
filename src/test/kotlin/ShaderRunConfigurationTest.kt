import com.intellij.execution.configurations.RuntimeConfigurationError
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import glsl.plugin.preview.run.settings.EMPTY_FILE_INPUT
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

    fun testCheckConfigurationFailsAndResetsFragmentFileWhenMissing() {
        val configuration = newConfiguration()
        configuration.setFragmentFile("file://this/file/does/not/exist.frag")

        assertThrows(RuntimeConfigurationError::class.java) {
            configuration.checkConfiguration()
        }
        assertEquals(EMPTY_FILE_INPUT, configuration.getFragmentFile())
    }

    fun testSetUniformNameMergesIntoExistingMappings() {
        val configuration = newConfiguration()
        configuration.setUniformName(UniformType.TIME, "uMyTime")

        configuration.setUniformName(UniformType.MOUSE, "uMyMouse")

        assertEquals(
            mapOf(UniformType.TIME to "uMyTime", UniformType.MOUSE to "uMyMouse"),
            configuration.getUniforms()
        )
    }

    fun testSetUniformNamesReplacesAllExistingMappings() {
        val configuration = newConfiguration()
        configuration.setUniformName(UniformType.TIME, "uMyTime")

        configuration.setUniformNames(mapOf(UniformType.MOUSE to "uMyMouse"))

        assertEquals(mapOf(UniformType.MOUSE to "uMyMouse"), configuration.getUniforms())
    }
}
