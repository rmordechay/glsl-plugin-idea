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
}
