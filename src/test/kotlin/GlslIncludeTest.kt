import com.intellij.testFramework.UsefulTestCase
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import glsl.psi.interfaces.*
import org.junit.Test

class GlslIncludeTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String {
        return "src/test/testData/include"
    }

    fun testReferenceFile1() {
        myFixture.configureByFiles("IncludeFile2.glsl")
        val reference = myFixture.getReferenceAtCaretPosition("IncludeFile1.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslSingleDeclaration::class.java)
        assertEquals("b", (resolve as GlslSingleDeclaration).name)
    }

    fun testReferenceFile2() {
        val reference = myFixture.getReferenceAtCaretPosition("IncludeFile3.glsl")
        assertNoThrowable { reference?.resolve() }
    }

    fun testReferenceFile3() {
        myFixture.configureByFiles("IncludeFile4.glsl", "IncludeFile5.glsl")
        val reference = myFixture.getReferenceAtCaretPosition("IncludeFile6.glsl")
        assertNoThrowable { reference?.resolve() }
    }
}
