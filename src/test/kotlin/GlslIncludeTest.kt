import com.intellij.testFramework.fixtures.BasePlatformTestCase
import glsl.plugin.reference.GlslFileReference
import org.junit.Test

class GlslIncludeTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String {
        return "src/test/testData/include"
    }

    @Test
    fun testReferenceFile1() {
        val reference = myFixture.getReferenceAtCaretPosition("IncludeFile1.glsl")
        assertInstanceOf(reference, GlslFileReference::class.java)
    }
}
