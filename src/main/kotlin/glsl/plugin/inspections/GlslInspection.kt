package glsl.plugin.inspections

import com.intellij.codeInspection.LocalInspectionTool
import glsl.data.GlslErrorCode

/**
 *
 */
abstract class GlslInspection: LocalInspectionTool() {
    abstract val errorMessageCode: GlslErrorCode?
}