package glsl.plugin.preview.run.settings

import com.intellij.icons.AllIcons
import com.intellij.ide.HelpTooltip
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.getOpenedProjects
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.RightGap
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.IconUtil
import com.intellij.util.ui.FormBuilder
import glsl.plugin.preview.SUPPORTED_SHADER_FILE_ENDINGS
import glsl.plugin.utils.idea.FileListCellRenderer
import java.awt.GridBagConstraints
import java.util.*
import javax.swing.JComboBox
import javax.swing.JComponent

/**
 * View controller for the run configuration dialog
 */
class ShaderSettingsEditor : SettingsEditor<ShaderRunConfiguration>() {

    val fileListbox = JComboBox(getOpenShaderFiles().toTypedArray()).apply {
        renderer = FileListCellRenderer
    }

    var uniforms: Map<UniformType, JBTextField> =
        UniformType.entries.associateWith { type -> JBTextField(type.defaultName, 15) };

    private val propertyGraph = PropertyGraph()
    val fragFileIsBroken = propertyGraph.property(false)


    /**
     * Transfers configuration state to ui
     */
    override fun resetEditorFrom(configuration: ShaderRunConfiguration) {
        val fileManager: VirtualFileManager = VirtualFileManager.getInstance()
        val fileEditorManager: FileEditorManager = FileEditorManager.getInstance(configuration.project);
        val currentFragFile = configuration.getFragmentFile();
        if (currentFragFile != null) {
            if (currentFragFile == EMPTY_FILE_INPUT) {
                fragFileIsBroken.set(true)
                fileListbox.selectedIndex = -1
            } else {
                fileListbox.selectedItem = Objects.requireNonNull(
                    fileManager.findFileByUrl(currentFragFile),
                    "Could not find file for $currentFragFile"
                )
                fragFileIsBroken.set(false) //its not broken but the config is new
            }
        } else {
            val currentFile = fileEditorManager.focusedEditor?.file
            if (currentFile?.extension in SUPPORTED_SHADER_FILE_ENDINGS) {
                fileListbox.selectedItem = currentFile
            }
            fragFileIsBroken.set(false)
        }
        configuration.getUniforms().forEach { (uniform, nameValue) ->
            uniforms[uniform]?.setText(nameValue)
        }
    }

    /**
     * Is called when user clicks "apply" on run config dialog.
     * Transfers ui state to run configuration
     */
    override fun applyEditorTo(configuration: ShaderRunConfiguration) {
        var selectedFile = fileListbox.selectedItem as VirtualFile?
        if (selectedFile != null) {
            fragFileIsBroken.set(false)
            configuration.setFragmentFile(selectedFile.url)
            for ((type, component) in uniforms) {
                configuration.setUniformName(type, component.text)
            }
        }
    }

    override fun createEditor(): JComponent {
        val smallErrorIcon = IconUtil.scale(
            AllIcons.General.ErrorDialog,
            null,
            0.8f
        ) //need that scale because setting the size or maximumsize to the icon has no effect
        val listboxRow: JBPanel<DialogPanel> = panel {
            row {
                cell(fileListbox).gap(RightGap.SMALL)
                icon(smallErrorIcon)
                    .visibleIf(fragFileIsBroken)
                    .applyToComponent {
                        toolTipText = "Can not find selected source file"
                    }
            }
        };

        val formBuilder = FormBuilder.createFormBuilder()
            .addSeparator()
            .addLabeledComponent("Fragment Shader File:", listboxRow)
            .addSeparator()
            .addComponent(JBLabel("Uniforms:"))

        //Uniforms
        val constraints = GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            weightx = 1.0
            gridy = 0
            gridx = 0
        }
        val uniformSectionLayout = java.awt.GridBagLayout() //maybe replace with kotlin ui dsl
        val uniformGrid = JBPanel<JBPanel<*>>(uniformSectionLayout)
        uniforms.forEach { (uniform, inputComponent) ->
            val label = JBLabel(uniform.label)
            val tooltip = HelpTooltip();
            tooltip.description = uniform.tooltip;
            tooltip.installOn(label);

            label.labelFor = inputComponent

            constraints.weightx = 0.0
            constraints.insets = java.awt.Insets(0, 0, 0, 0)
            uniformGrid.add(label, constraints)

            constraints.gridx++
            constraints.weightx = 1.0
            constraints.insets = java.awt.Insets(0, 30, 0, 30)
            uniformGrid.add(inputComponent, constraints)

            constraints.gridx++
            if (constraints.gridx == 4) {
                constraints.gridx = 0
                constraints.gridy++
            }
        }
        formBuilder.addComponent(uniformGrid)
        return formBuilder.panel
    }

    private fun getOpenShaderFiles(): List<VirtualFile> {
        val availableFiles: MutableList<VirtualFile> = ArrayList()
        getOpenedProjects().forEach { project ->
            val fileEditorManager: FileEditorManager = FileEditorManager.getInstance(project);
            val openFiles: Set<VirtualFile> = fileEditorManager.openFiles.toSet()
            availableFiles += openFiles.filter { file -> file.extension in SUPPORTED_SHADER_FILE_ENDINGS }
        }
        return availableFiles;
    }
}