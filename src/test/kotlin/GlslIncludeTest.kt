import com.intellij.testFramework.fixtures.BasePlatformTestCase
import glsl.psi.interfaces.GlslSingleDeclaration
import org.junit.Test

class GlslIncludeTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String {
        return "src/test/testData/include"
    }

    @Test
    fun testReferenceFile1() {
        val reference = myFixture.getReferenceAtCaretPosition("IncludeFile1.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslSingleDeclaration::class.java)
    }
}
