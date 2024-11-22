package glsl.plugin.editor.style

import com.intellij.application.options.IndentOptionsEditor
import com.intellij.application.options.SmartIndentOptionsEditor
import com.intellij.lang.Language
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable
import com.intellij.psi.codeStyle.CustomCodeStyleSettings
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider
import glsl.plugin.language.GlslLanguage
import glsl.plugin.utils.GlslUtils

class GlslCodeStyleSettingsProvider : LanguageCodeStyleSettingsProvider() {
    /**
    *
    */
    override fun getLanguage(): Language {
        return GlslLanguage.INSTANCE
    }

    /**
    *
    */
    override fun getCodeSample(settingsType: SettingsType): String? {
        return GlslUtils.getResourceFileAsString("formatter/sample-code.txt")
    }

    /**
    *
    */
    override fun getIndentOptionsEditor(): IndentOptionsEditor {
        return SmartIndentOptionsEditor()
    }

    /**
    *
    */
    override fun customizeSettings(consumer: CodeStyleSettingsCustomizable, settingsType: SettingsType) {
        consumer.showStandardOptions(
            "SPACE_BEFORE_SEMICOLON",
            "SPACE_BEFORE_SEMICOLON",
            "SPACE_AFTER_SEMICOLON",
            "SPACE_BEFORE_QUEST",
            "SPACE_AFTER_QUEST",
            "SPACE_BEFORE_COLON",
            "SPACE_AFTER_COLON",
            "SPACE_BEFORE_COMMA",
            "SPACE_AFTER_COMMA",
            "SPACE_AROUND_ASSIGNMENT_OPERATORS",
            "SPACE_AROUND_ADDITIVE_OPERATORS",
            "SPACE_AROUND_MULTIPLICATIVE_OPERATORS",
            "SPACE_AROUND_EQUALITY_OPERATORS",
            "SPACE_AROUND_RELATIONAL_OPERATORS",
            "SPACE_AROUND_LOGICAL_OPERATORS",
            "SPACE_AROUND_BITWISE_OPERATORS",
            "SPACE_AROUND_SHIFT_OPERATORS",
            "SPACE_WITHIN_PARENTHESES",
            "SPACE_WITHIN_BRACKETS",
        )

        consumer.renameStandardOption("SPACE_WITHIN_PARENTHESES", "Parentheses")
    }
}

/**
 *
 */
class GlslCodeStyleSettings(settings: CodeStyleSettings) : CustomCodeStyleSettings("GlslCodeStyleSetting", settings)