package glsl.plugin.code.formatter

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.psi.TokenType
import com.intellij.psi.formatter.common.AbstractBlock
import com.intellij.psi.tree.IFileElementType
import glsl.GlslTypes.*
import glsl.data.GlslDefinitions.BACKSLASH
import glsl.psi.interfaces.GlslExprNoAssignment

val COMPOUND_STATEMENTS = listOf(
    COMPOUND_STATEMENT,
    COMPOUND_STATEMENT_NO_NEW_SCOPE,
    BLOCK_STRUCTURE,
    STRUCT_SPECIFIER
)

class GlslBlock(
    node: ASTNode,
    wrap: Wrap?,
    myAlignment: Alignment?,
    private val mySpacing: SpacingBuilder,
    private val myIndent: Indent?
) : AbstractBlock(node, wrap, myAlignment) {

    /**
     *
     */
    override fun getIndent(): Indent? {
        return myIndent
    }

    /**
     *
     */
    override fun getSpacing(child1: Block?, child2: Block): Spacing? {
        return mySpacing.getSpacing(this, child1, child2)
    }

    /**
     *
     */
    override fun isLeaf(): Boolean {
        return node.firstChildNode == null
    }

    /**
     * This method is called after ENTER was pressed.
     */
    override fun getChildAttributes(newChildIndex: Int): ChildAttributes {
        if (node.elementType in COMPOUND_STATEMENTS) {
            return ChildAttributes(Indent.getNormalIndent(), null)
        }
        return super.getChildAttributes(newChildIndex)
    }

    /**
     *
     */
    override fun buildChildren(): List<Block> {
        val blocks = arrayListOf<Block>()
        val newAlignment = Alignment.createAlignment()
        var childNode = node.firstChildNode
        while (childNode != null) {
            if (childNode.elementType != TokenType.WHITE_SPACE || childNode.text.contains(BACKSLASH)) {
                val childBlock = getChildBlock(childNode, newAlignment, Alignment.createAlignment())
                blocks.add(childBlock)
            }
            childNode = childNode.treeNext
        }
        return blocks
    }

    /**
     *
     */
    private fun getChildBlock(child: ASTNode, newAlignment: Alignment, switchStatementAlignment: Alignment): Block {
        val currentElementType = node.elementType
        val childElementType = child.elementType
        val wrap = Wrap.createWrap(WrapType.NONE, false)
        when {
            currentElementType == SWITCH_STATEMENT -> {
                return getSwitchStatementBlock(child, newAlignment, switchStatementAlignment)
            }
            currentElementType == ITERATION_STATEMENT && node.firstChildNode.elementType == DO -> {
                return getDoWhileStatementBlock(child, newAlignment)
            }
            currentElementType == FUNCTION_CALL && child.psi is GlslExprNoAssignment -> {
                return GlslBlock(child, wrap, newAlignment, mySpacing, Indent.getNormalIndent())
            }
            childElementType in listOf(STATEMENT, STRUCT_DECLARATION, PARAMETER_DECLARATOR) -> {
                return GlslBlock(child, wrap, newAlignment, mySpacing, Indent.getNormalIndent())
            }
            childElementType == EXTERNAL_DECLARATION -> {
                return GlslBlock(child, wrap, newAlignment, mySpacing, Indent.getNoneIndent())
            }
            childElementType == LINE_COMMENT -> {
                return getCommentBlock(child, newAlignment)
            }
            else -> return GlslBlock(child, wrap, null, mySpacing, Indent.getNoneIndent())
        }
    }

    /**
     *
     */
    private fun getCommentBlock(child: ASTNode, newAlignment: Alignment): GlslBlock {
        val parentType = node.elementType
        return when (parentType) {
            is IFileElementType -> {
                GlslBlock(child, wrap, newAlignment, mySpacing, Indent.getNoneIndent())
            }
            in COMPOUND_STATEMENTS -> {
                GlslBlock(child, wrap, newAlignment, mySpacing, Indent.getNormalIndent())
            }
            else -> {
                GlslBlock(child, wrap, null, mySpacing, Indent.getNoneIndent())
            }
        }
    }

    /**
     *  Because switch_statement has a tricky grammar, it needs a special handling (in particular, because case_label is
     *  not a parent of the other child statements of switch_statement).
     */
    private fun getSwitchStatementBlock(switchStmtNode: ASTNode, stmtAlignment: Alignment, caseAlignment: Alignment): GlslBlock {
        return if (switchStmtNode.elementType == STATEMENT) {
            val statementChild = switchStmtNode.firstChildNode.elementType
            if (statementChild == CASE_LABEL) {
                GlslBlock(switchStmtNode, wrap, caseAlignment, mySpacing, Indent.getNormalIndent())
            } else {
                GlslBlock(switchStmtNode, wrap, stmtAlignment, mySpacing, Indent.getSpaceIndent(8))
            }
        } else {
            GlslBlock(switchStmtNode, wrap, null, mySpacing, Indent.getNoneIndent())
        }
    }

    /**
     *
     */
    private fun getDoWhileStatementBlock(doWhileElement: ASTNode, newAlignment: Alignment): GlslBlock {
        if (doWhileElement.elementType == STATEMENT) {
            if (doWhileElement.firstChildNode.elementType == COMPOUND_STATEMENT) {
                return GlslBlock(doWhileElement, wrap, null, mySpacing, Indent.getNoneIndent())
            }
            return GlslBlock(doWhileElement, wrap, newAlignment, mySpacing, Indent.getNormalIndent())
        }
        return GlslBlock(doWhileElement, wrap, null, mySpacing, Indent.getNoneIndent())
    }
}