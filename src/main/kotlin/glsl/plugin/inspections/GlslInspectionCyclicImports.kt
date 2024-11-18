package glsl.plugin.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import glsl.psi.interfaces.GlslPpIncludeDeclaration
import glsl.psi.interfaces.GlslVisitor

/**
 *
 */
class GlslInspectionCyclicImports : GlslInspection() {
    override val errorMessageCode = GlslErrorCode.PRIMITIVE_CONSTRUCTOR_ZERO_ARGUMENTS

    /**
     *
     */
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : GlslVisitor() {
            override fun visitPpIncludeDeclaration(o: GlslPpIncludeDeclaration) {
                super.visitPpIncludeDeclaration(o)
            }
        }
    }
}