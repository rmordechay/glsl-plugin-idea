package glsl.plugin.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.patterns.StandardPatterns.or
import glsl.GlslTypes.*
import glsl.data.GlslDefinitions
import glsl.data.GlslTokenSets
import glsl.plugin.completion.GlslPostWhiteSpaceCompletion.Reject
import glsl.plugin.completion.GlslPostWhiteSpaceCompletion.Require
import glsl.plugin.utils.GlslUtils
import glsl.psi.interfaces.*


/**
 *
 */
class GlslCompletionContributor : CompletionContributor() {
    // Keywords sets
    private val selectionKeywords = GlslUtils.getTokenSetAsStrings(GlslTokenSets.SELECTION)
    private val iterationKeywords = GlslUtils.getTokenSetAsStrings(GlslTokenSets.ITERATION)
    private val funcJumpsKeywords = GlslUtils.getTokenSetAsStrings(GlslTokenSets.FUNC_JUMPS)
    private val iterationJumpsKeywords = GlslUtils.getTokenSetAsStrings(GlslTokenSets.ITERATION_JUMPS)
    private val typeQualifiers = GlslUtils.getTokenSetAsStrings(GlslTokenSets.TYPE_QUALIFIERS)

    // Patterns
    private val numeric = or(
        psiElement(INTCONSTANT),
        psiElement(UINTCONSTANT),
        psiElement(FLOATCONSTANT),
        psiElement(DOUBLECONSTANT),
    )

    private val afterDot = psiElement().afterLeaf(".")
    private val afterPpLiteral = psiElement().afterLeaf("#")
    private val insidePpStatement = psiElement().inside(GlslPpStatement::class.java)
    private val afterVersion = psiElement().afterLeaf(psiElement(INTCONSTANT).afterLeaf(psiElement(PP_VERSION)))
    private val afterPpVersion = psiElement().afterLeaf(psiElement(PP_VERSION))

    private val insideIteration = psiElement()
        .inside(psiElement(GlslCompoundStatementNoNewScope::class.java).withParent(GlslIterationStatement::class.java))
        .andNot(psiElement().afterLeaf(numeric))
        .andNot(afterDot)

    private val insideTypeSpecifier = psiElement(IDENTIFIER)
        .withParent(GlslTypeName::class.java)
        .andNot(psiElement().afterLeaf(numeric))
        .inside(GlslTypeSpecifier::class.java)

    private val insideExpression = psiElement(IDENTIFIER)
        .andNot(psiElement().afterLeaf(numeric))
        .andNot(afterDot)
        .inside(GlslExpr::class.java)

    private val statementBeginning = psiElement()
        .andNot(psiElement().afterLeaf(numeric))
        .atStartOf(psiElement(GlslStatement::class.java))
        .andNot(afterDot)
        .andNot(insidePpStatement)

    private val externalDeclarationBeginning = psiElement(IDENTIFIER)
        .andNot(psiElement().afterLeaf(numeric))
        .atStartOf(psiElement(GlslExternalDeclaration::class.java))
        .andNot(afterDot)
        .andNot(insidePpStatement)

    private val paramBeginning = psiElement()
        .andNot(psiElement().afterLeaf(numeric))
        .inside(psiElement(GlslFuncHeaderWithParams::class.java))
        .afterLeaf("(", ",")

    private val typeQualifiersPattern = or(
        statementBeginning,
        externalDeclarationBeginning,
        paramBeginning
    )

    private val insideInclude = psiElement(STRING_LITERAL)
        .inside(GlslPpIncludeDeclaration::class.java)

    init {
        extend(CompletionType.BASIC, typeQualifiersPattern, GlslGenericCompletion(*typeQualifiers))
        extend(CompletionType.BASIC, statementBeginning, GlslGenericCompletion(*selectionKeywords, *iterationKeywords, *funcJumpsKeywords))
        extend(CompletionType.BASIC, insideIteration, GlslGenericCompletion(*iterationJumpsKeywords))
        extend(CompletionType.BASIC, afterPpLiteral, GlslPpCompletion())
        extend(CompletionType.BASIC, afterVersion, GlslGenericCompletion(*GlslDefinitions.VERSIONS, whitespace = Reject))
        extend(CompletionType.BASIC, afterPpVersion, GlslGenericCompletion(*GlslDefinitions.VERSIONS, whitespace = Require))
        extend(CompletionType.BASIC, afterVersion, GlslGenericCompletion(*GlslDefinitions.VERSION_MODES, whitespace = Require))
        extend(CompletionType.BASIC, insideInclude, GlslIncludeStatementCompletion())
        // Builtin objects
        extend(CompletionType.BASIC, insideTypeSpecifier, GlslBuiltinTypesCompletion())
        extend(CompletionType.BASIC, insideExpression, GlslBuiltinFuncCompletion())

    }
}

