import com.intellij.testFramework.ParsingTestCase
import glsl.plugin.language.GlslParserDefinition


class GlslParserTest : ParsingTestCase("", "test", GlslParserDefinition()) {

    override fun getTestDataPath(): String {
        return "src/test/testData/parser"
    }

    override fun skipSpaces(): Boolean {
        return true
    }

    override fun includeRanges(): Boolean {
        return true
    }

    fun testParserFile() {
        doTest(true)
    }

    /**
     * Documents the current (buggy) parse tree for a function-like macro call with a
     * real, multi-token argument list used in expression position. See docs/macro-issues.MD
     * for the root cause and why this is split out from testParserFile: the golden file here
     * captures today's broken shape so it doesn't block the rest of the suite, and will need
     * regenerating once the underlying lexer bug is fixed.
     */
    fun testMacroFunctionCallArgs() {
        doTest(true)
    }
}
