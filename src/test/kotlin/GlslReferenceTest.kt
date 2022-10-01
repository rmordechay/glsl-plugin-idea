import com.intellij.testFramework.fixtures.BasePlatformTestCase
import glsl.psi.interfaces.*
import org.junit.Test

class GlslReferenceTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String {
        return "src/test/testData/reference"
    }

    @Test
    fun testReferenceFile1() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile1.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslSingleDeclaration::class.java)
    }

    @Test
    fun testReferenceFile2() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile2.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslStructDeclarator::class.java)
        assertEquals("structMember", resolve?.text)
    }

    @Test
    fun testReferenceFile3() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile3.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslStructDeclarator::class.java)
        assertEquals("c", resolve?.text)
    }

    @Test
    fun testReferenceFile4() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile4.glsl")
        val resolve = reference?.resolve()
        assertNull(resolve)
    }


    @Test
    fun testReferenceFile5() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile5.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslSingleDeclaration::class.java)
        assertEquals("i", (resolve as GlslSingleDeclaration).name)
        assertInstanceOf(resolve.parent?.parent, GlslIterationStatement::class.java)
    }

    @Test
    fun testReferenceFile6() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile6.glsl")
        val resolve = reference?.resolve()
        assertNull(resolve)
    }

    @Test
    fun testReferenceFile7() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile7.glsl")
        val resolve = reference?.resolve()
        assertNull(resolve)
    }

    @Test
    fun testReferenceFile8() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile8.glsl")
        val resolve = reference?.resolve()
        assertNull(resolve)
    }

    @Test
    fun testReferenceFile9() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile9.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslSingleDeclaration::class.java)
        assertEquals("dummy", (resolve as GlslSingleDeclaration).name)
    }

    @Test
    fun testReferenceFile10() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile10.glsl")
        val resolve = reference?.resolve()
        assertNull(resolve)
    }

    @Test
    fun testReferenceFile11() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile11.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslParameterDeclarator::class.java)
        assertEquals("param1", (resolve as GlslParameterDeclarator).name)
        assertEquals("int", resolve.getAssociatedType()?.getTypeText())
    }

    @Test
    fun testReferenceFile12() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile12.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslParameterDeclarator::class.java)
        assertEquals("param1", (resolve as GlslParameterDeclarator).name)
        assertNotNull(resolve.getAssociatedType())
        assertEquals("int", resolve.getAssociatedType()?.getTypeText())
    }

    @Test
    fun testReferenceFile13() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile13.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslStructSpecifier::class.java)
        assertEquals("A", (resolve as GlslStructSpecifier).name)
    }

    @Test
    fun testReferenceFile14() {
        myFixture.configureByFile("ReferenceFile14.glsl")
        val parameterDeclarator = myFixture.elementAtCaret as? GlslParameterDeclarator
        val variableType1 = parameterDeclarator?.getAssociatedType()
        val variableType2 = parameterDeclarator?.getAssociatedType()
        assertNotNull(variableType1)
        assertNotNull(variableType2)
        assertEquals("int", variableType1?.getTypeText())
        assertEquals("int", variableType2?.getTypeText())
    }

    @Test
    fun testReferenceFile15() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile15.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslStructDeclarator::class.java)
        assertEquals("inner", (resolve as GlslStructDeclarator).name)
    }

    @Test
    fun testReferenceFile16() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile16.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslStructDeclarator::class.java)
        assertEquals("vel", (resolve as GlslStructDeclarator).name)
    }

    @Test
    fun testReferenceFile17() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile17.comp")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslStructDeclarator::class.java)
        assertEquals("gl_LocalInvocationID", (resolve as GlslStructDeclarator).name)
    }

    @Test
    fun testReferenceFile18() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile18.comp")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslStructDeclarator::class.java)
        assertEquals("x", (resolve as GlslStructDeclarator).name)
    }

    @Test
    fun testReferenceFile19() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile19.frag")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslStructDeclarator::class.java)
        assertEquals("gl_FragCoord", (resolve as GlslStructDeclarator).name)
    }

    @Test
    fun testReferenceFile20() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile20.frag")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslSingleDeclaration::class.java)
        assertEquals("gl_MaxGeometryInputComponents", (resolve as GlslSingleDeclaration).name)
    }

    @Test
    fun testReferenceFile21() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile21.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslStructDeclarator::class.java)
        assertEquals("xy", (resolve as GlslStructDeclarator).name)
    }

    @Test
    fun testReferenceFile22() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile22.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslStructDeclarator::class.java)
        assertEquals("w", (resolve as GlslStructDeclarator).name)
    }

    @Test
    fun testReferenceFile23() {
//        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile23.glsl")
//        val resolve = reference?.resolve()
//        assertInstanceOf(resolve, GlslStructDeclarator::class.java)
//        assertEquals("xyz", (resolve as GlslStructDeclarator).name)
    }

    @Test
    fun testReferenceFile24() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile24.glsl")
        val resolve = reference?.resolve()
        assertNull(resolve)
    }

    @Test
    fun testReferenceFile25() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile25.glsl")
        val resolve = reference?.resolve()
        assertNull(resolve)
    }

    @Test
    fun testReferenceFile26() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile26.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslStructDeclarator::class.java)
    }

    @Test
    fun testReferenceFile27() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile27.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslStructDeclarator::class.java)
        assertEquals("xz", (resolve as GlslStructDeclarator).name)
    }

    @Test
    fun testReferenceFile28() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile28.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslSingleDeclaration::class.java)
        assertEquals("arr", (resolve as GlslSingleDeclaration).name)
    }

    @Test
    fun testReferenceFile29() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile29.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslSingleDeclaration::class.java)
        assertEquals("a", (resolve as GlslSingleDeclaration).name)
    }

    @Test
    fun testReferenceFile30() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile30.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslPpDefineParam::class.java)
        assertEquals("x", (resolve as GlslPpDefineParam).name)
    }

    @Test
    fun testReferenceFile31() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile31.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslPpDefineFunction::class.java)
        assertEquals("func", (resolve as GlslPpDefineFunction).name)
    }

    @Test
    fun testReferenceFile32() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile32.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslPpSingleDeclaration::class.java)
        assertEquals("VAR", (resolve as GlslPpSingleDeclaration).name)
    }

    @Test
    fun testFindUsageFile1() {
        val testFindUsages = myFixture.testFindUsages("FindUsageFile1.glsl")
        assertEquals(3, testFindUsages.size)
    }

    @Test
    fun testFindUsageFile2() {
        assertThrows(AssertionError::class.java) { myFixture.testFindUsages("FindUsageFile2.glsl") }
    }
}
