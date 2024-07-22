package glsl.plugin.inspections

import com.intellij.codeInspection.LocalInspectionTool

/**
 *
 */
abstract class GlslInspection: LocalInspectionTool() {
    abstract val errorMessageCode: GlslErrorCode?
}