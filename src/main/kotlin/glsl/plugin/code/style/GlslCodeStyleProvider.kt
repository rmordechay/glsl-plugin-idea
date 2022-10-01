package glsl.plugin.code.style

import com.intellij.application.options.CodeStyleAbstractConfigurable
import com.intellij.application.options.CodeStyleAbstractPanel
import com.intellij.application.options.TabbedLanguageCodeStylePanel
import com.intellij.psi.codeStyle.CodeStyleConfigurable
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider
import com.intellij.psi.codeStyle.CustomCodeStyleSettings
import glsl.plugin.language.GlslLanguage

/**
 *
 */
class GlslCodeStyleProvider : CodeStyleSettingsProvider() {

    /**
    *
    */
    override fun getConfigurableDisplayName(): String {
        return "GLSL"
    }

    /**
    *
    */
    override fun createConfigurable(settings: CodeStyleSettings, modelSettings: CodeStyleSettings): CodeStyleConfigurable {
        return GlslCodeStyleConfigurable(settings, modelSettings, configurableDisplayName)
    }

    /**
    *
    */
    override fun createCustomSettings(settings: CodeStyleSettings): CustomCodeStyleSettings {
        return GlslCodeStyleSettings(settings)
    }
}

/**
 *
 */
class GlslCodeStyleConfigurable(settings: CodeStyleSettings, modelSettings: CodeStyleSettings, displayName: String)
    : CodeStyleAbstractConfigurable(settings, modelSettings, displayName) {

    /**
    *
    */
    override fun createPanel(settings: CodeStyleSettings): CodeStyleAbstractPanel {
        return GlslCodeStylePanel(currentSettings, settings)
    }

    /**
     *
     */
    inner class GlslCodeStylePanel(currentSettings: CodeStyleSettings, settings: CodeStyleSettings) : TabbedLanguageCodeStylePanel(GlslLanguage.INSTANCE, currentSettings, settings)
}