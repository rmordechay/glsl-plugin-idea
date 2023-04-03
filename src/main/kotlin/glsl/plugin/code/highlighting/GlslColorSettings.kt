package glsl.plugin.code.highlighting

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import glsl.plugin.language.GlslIcon
import glsl.plugin.utils.GlslUtils
import javax.swing.Icon

/**
 *
 */
class GlslColorSettings : ColorSettingsPage {

    companion object {
        private val DESCRIPTORS = arrayOf(
            AttributesDescriptor("Comment // Multi-line", GlslTextAttributes.MULTILINE_COMMENT_TEXT_ATTR),
            AttributesDescriptor("Comment // One-line", GlslTextAttributes.LINE_COMMENT_TEXT_ATTR),
            AttributesDescriptor("Identifier // User defined identifier", GlslTextAttributes.VARIABLE_TEXT_ATTR),
            AttributesDescriptor("Identifier // Builtin identifier", GlslTextAttributes.BUILTIN_NAME_TEXT_ATTR),
            AttributesDescriptor("Identifier // Builtin global constant", GlslTextAttributes.BUILTIN_GLOBAL_CONSTANTS),
            AttributesDescriptor("String", GlslTextAttributes.STRING_TEXT_ATTR),
            AttributesDescriptor("Number", GlslTextAttributes.NUMBERS_TEXT_ATTR),
            AttributesDescriptor("Keyword", GlslTextAttributes.KEYWORD_TEXT_ATTR),
            AttributesDescriptor("User-defined type", GlslTextAttributes.USER_DEFINED_TYPE_TEXT_ATTR),
            AttributesDescriptor("Function // Function call and declaration", GlslTextAttributes.FUNC_TEXT_ATTR),
            AttributesDescriptor("Function // Function parameter", GlslTextAttributes.FUNC_PARAM_TEXT_ATTR),
            AttributesDescriptor("Preprocessor // Directive", GlslTextAttributes.DIRECTIVE_TEXT_ATTR),
            AttributesDescriptor("Preprocessor // Identifier", GlslTextAttributes.PP_DEFINE_DECLARATION),
            AttributesDescriptor("Builtin type", GlslTextAttributes.BUILTIN_TYPE_TEXT_ATTR),
            AttributesDescriptor("Operators", GlslTextAttributes.OPERATORS_TEXT_ATTR),
            AttributesDescriptor("Bad character", GlslTextAttributes.BAD_CHARACTER_TEXT_ATTR),
        )

        private val ADDITIONAL_DESCRIPTORS = mutableMapOf(
            "fn" to GlslTextAttributes.FUNC_TEXT_ATTR,
            "fp" to GlslTextAttributes.FUNC_PARAM_TEXT_ATTR,
            "bi" to GlslTextAttributes.BUILTIN_NAME_TEXT_ATTR,
            "gc" to GlslTextAttributes.BUILTIN_GLOBAL_CONSTANTS,
            "v" to GlslTextAttributes.VARIABLE_TEXT_ATTR,
            "si" to GlslTextAttributes.STRUCT_TYPE_TEXT_ATTR,
            "udt" to GlslTextAttributes.USER_DEFINED_TYPE_TEXT_ATTR,
            "d" to GlslTextAttributes.DIRECTIVE_TEXT_ATTR,
            "ppd" to GlslTextAttributes.PP_DEFINE_DECLARATION,
        )
    }

    /**
    *
    */
    override fun getAttributeDescriptors(): Array<AttributesDescriptor> {
        return DESCRIPTORS
    }

    /**
    *
    */
    override fun getColorDescriptors(): Array<ColorDescriptor> {
        return ColorDescriptor.EMPTY_ARRAY
    }

    /**
    *
    */
    override fun getDisplayName(): String {
        return "GLSL"
    }

    /**
    *
    */
    override fun getIcon(): Icon {
        return GlslIcon.PLUGIN_FILE_ICON
    }

    /**
    *
    */
    override fun getHighlighter(): SyntaxHighlighter {
        return GlslSyntaxHighlighter()
    }

    /**
    *
    */
    override fun getDemoText(): String {
        return GlslUtils.getResourceFileAsString("colors/color-demo-text.txt") ?: ""
    }

    /**
    *
    */
    override fun getAdditionalHighlightingTagToDescriptorMap(): MutableMap<String, TextAttributesKey> {
        return ADDITIONAL_DESCRIPTORS
    }
}