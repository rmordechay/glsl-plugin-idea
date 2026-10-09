import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.LightVirtualFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import glsl.plugin.preview.run.settings.ShaderRunConfiguration
import glsl.plugin.preview.run.settings.ShaderRunConfigurationFactory
import glsl.plugin.preview.run.settings.ShaderRunConfigurationType
import glsl.plugin.preview.run.settings.ShaderSettingsEditor

/**
 * Covers the run configuration dialog ([ShaderSettingsEditor]) - in particular which fragment shader files it
 * offers to choose from.
 */
class ShaderSettingsEditorTest : BasePlatformTestCase() {

    fun testFileListOnlyOffersShaderFilesOpenInTheConfigurationsOwnProject() {
        val ownFile = myFixture.configureByText("own.frag", "void main() {}").virtualFile

        val otherFile = LightVirtualFile("other.frag", "void main() {}")
        var offeredFiles = emptyList<VirtualFile>()
        withTemporaryProject("otherProject") { otherProject ->
            try {
                FileEditorManager.getInstance(otherProject).openFile(otherFile, false)
                assertTrue(
                    "expected the other project to have its shader file open, or this test proves nothing",
                    FileEditorManager.getInstance(otherProject).isFileOpen(otherFile)
                )

                offeredFiles = offeredFragmentFiles(newConfiguration())
            } finally {
                // Closing the other project checks that no editor anywhere is still open - including this project's.
                FileEditorManager.getInstance(project).closeFile(ownFile)
            }
        }

        assertTrue("expected the open shader file of this project to be offered", ownFile in offeredFiles)
        assertFalse(
            "expected shader files open in another project not to be offered, got ${offeredFiles.map { it.name }}",
            otherFile in offeredFiles
        )
    }

    fun testFileListOnlyOffersShaderFiles() {
        val shaderFile = myFixture.configureByText("shader.frag", "void main() {}").virtualFile
        val textFile = myFixture.configureByText("notes.txt", "not a shader").virtualFile

        val offeredFiles = offeredFragmentFiles(newConfiguration())

        assertTrue("expected an open .frag file to be offered", shaderFile in offeredFiles)
        assertFalse("expected an open non-shader file not to be offered", textFile in offeredFiles)
    }

    private fun newConfiguration(): ShaderRunConfiguration {
        val factory = ShaderRunConfigurationFactory(ShaderRunConfigurationType())
        return ShaderRunConfiguration(project, factory, "Fragment Shader")
    }

    /**
     * Returns the files the run configuration dialog of [configuration] lets the user pick as fragment shader.
     */
    private fun offeredFragmentFiles(configuration: ShaderRunConfiguration): List<VirtualFile> {
        val editor = configuration.configurationEditor as ShaderSettingsEditor
        val model = editor.fileListbox.model
        return (0 until model.size).map { model.getElementAt(it) }
    }
}
