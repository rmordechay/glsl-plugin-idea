import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import glsl.psi.interfaces.GlslSingleDeclaration
import glsl.psi.interfaces.GlslStructSpecifier
import org.junit.Test

class GlslIncludeTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String {
        return "src/test/testData/include"
    }

    @Test
    fun testReferenceFile1() {
        val reference = myFixture.getReferenceAtCaretPosition("IncludeFile1.glsl")
        assertInstanceOf(reference, FileReference::class.java)
    }

    @Test
    fun testReferenceFile2() {
        myFixture.configureByFile("IncludeFile4.glsl")
        val reference = myFixture.getReferenceAtCaretPosition("IncludeFile3.glsl")
        assertInstanceOf(reference?.resolve(), GlslSingleDeclaration::class.java)
    }

    @Test
    fun testReferenceFile3() {
        myFixture.configureByFile("IncludeFile5.glsl")
        val reference = myFixture.getReferenceAtCaretPosition("IncludeFile6.glsl")
        assertInstanceOf(reference?.resolve(), GlslStructSpecifier::class.java)
    }
}
