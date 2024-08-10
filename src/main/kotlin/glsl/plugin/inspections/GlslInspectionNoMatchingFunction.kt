package glsl.plugin.inspections

//import com.intellij.codeInspection.ProblemHighlightType
//import com.intellij.codeInspection.ProblemsHolder
//import com.intellij.psi.PsiElementVisitor
//import glsl.psi.impl.GlslFunctionDeclaratorImpl
//import glsl.psi.interfaces.GlslFunctionCall
//import glsl.psi.interfaces.GlslVisitor
//
///**
// *
// */
//class GlslInspectionNoMatchingFunction : GlslInspection() {
//    override val errorMessageCode = GlslErrorCode.NO_MATCHING_FUNCTION_CALL
//
//    /**
//     *
//     */
//    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
//        return object : GlslVisitor() {
//            override fun visitFunctionCall(functionCall: GlslFunctionCall) {
//                val funcIdentifier = functionCall.variableIdentifier ?: return
//                val functionDeclarator = funcIdentifier.resolveReference() as? GlslFunctionDeclaratorImpl ?: return
//                val exprTypes = functionCall.exprNoAssignmentList.map { it.getExprType() }
//                val actualTypesString = exprTypes.mapNotNull { it?.name }.joinToString(", ")
//                val msg = errorMessageCode.message.format(funcIdentifier.getName(), actualTypesString)
//                holder.registerProblem(functionCall, msg, ProblemHighlightType.GENERIC_ERROR)
//            }
//        }
//    }
//}