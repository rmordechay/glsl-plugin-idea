import com.intellij.testFramework.fixtures.BasePlatformTestCase
import glsl.plugin.preview.run.settings.EMPTY_FILE_INPUT
import glsl.plugin.preview.run.settings.FragShaderRunOptions
import glsl.plugin.preview.run.settings.UniformType

/**
 * Covers [FragShaderRunOptions]'s uniform-mapping (de)serialization and fragment-document
 * lookup - the parts of the shader preview/run feature that don't need a live OpenGL context.
 */
class FragShaderRunOptionsTest : BasePlatformTestCase() {

    fun testUniformMappingsDefaultToEmptyWhenUnset() {
        val options = FragShaderRunOptions()
        assertEquals(emptyMap<UniformType, String>(), options.getUniformMappings())
    }

    fun testUniformMappingsRoundTripThroughSetUniformMappings() {
        val options = FragShaderRunOptions()
        val mappings = mapOf(UniformType.TIME to "uMyTime", UniformType.RESOLUTION to "uMyRes")

        options.setUniformMappings(mappings)

        assertEquals(mappings, options.getUniformMappings())
    }

    fun testSetUniformNameAddsANewEntryWithoutDisturbingOthers() {
        val options = FragShaderRunOptions()
        options.setUniformMappings(mapOf(UniformType.TIME to "uMyTime"))

        options.setUniformName(UniformType.MOUSE, "uMyMouse")

        assertEquals(
            mapOf(UniformType.TIME to "uMyTime", UniformType.MOUSE to "uMyMouse"),
            options.getUniformMappings()
        )
    }

    fun testSetUniformNameOverwritesExistingEntry() {
        val options = FragShaderRunOptions()
        options.setUniformMappings(mapOf(UniformType.TIME to "uOld"))

        options.setUniformName(UniformType.TIME, "uNew")

        assertEquals(mapOf(UniformType.TIME to "uNew"), options.getUniformMappings())
    }

    fun testGetFragDocumentReturnsDocumentForConfiguredFile() {
        val file = myFixture.configureByText("preview.frag", "void main() {}").virtualFile
        val options = FragShaderRunOptions()
        options.fragmentFile = file.url

        val document = options.getFragDocument()

        assertEquals("void main() {}", document.text)
    }

    fun testGetFragDocumentThrowsWhenFileCannotBeFound() {
        val options = FragShaderRunOptions()
        options.fragmentFile = "file://this/file/does/not/exist.frag"

        assertThrows(NullPointerException::class.java) {
            options.getFragDocument()
        }
        // Sanity check that the "not found" sentinel is a distinct value from a real path.
        assertNotSame(EMPTY_FILE_INPUT, options.fragmentFile)
    }
}
