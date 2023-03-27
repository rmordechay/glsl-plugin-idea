import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.Test

class GlslIncludeTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String {
        return "src/test/testData/include"
    }

    @Test
    fun testReferenceFile1() {
        val reference = myFixture.getReferenceAtCaretPosition("IncludeFile1.glsl")
        reference?.resolve()
        println()
    }
}
