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
    fun testIncludeFile1() {
        val reference = myFixture.getReferenceAtCaretPosition("IncludeFile1.glsl")
        assertInstanceOf(reference, FileReference::class.java)
    }

    @Test
    fun testIncludeFile2() {
        myFixture.configureByFile("IncludeFile4.glsl")
        val reference = myFixture.getReferenceAtCaretPosition("IncludeFile3.glsl")
        assertInstanceOf(reference?.resolve(), GlslSingleDeclaration::class.java)
    }

    @Test
    fun testIncludeFile3() {
        myFixture.configureByFile("IncludeFile6.glsl")
        val reference = myFixture.getReferenceAtCaretPosition("IncludeFile5.glsl")
        assertInstanceOf(reference?.resolve(), GlslStructSpecifier::class.java)
    }

    @Test
    fun testIncludeFile4() {
        myFixture.configureByFile("IncludeFile7.glsl")
        myFixture.configureByFile("IncludeFile8.glsl")
        val reference = myFixture.getReferenceAtCaretPosition("IncludeFile9.glsl")
        assertNull(reference?.resolve())
    }

    @Test
    fun testIncludeFile5() {
        val reference = myFixture.getReferenceAtCaretPosition("IncludeFile10.glsl")
        assertNull(reference?.resolve())
    }
}
