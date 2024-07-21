import com.intellij.testFramework.fixtures.BasePlatformTestCase
import glsl.psi.interfaces.*

class GlslReferenceTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String {
        return "src/test/testData/reference"
    }

    fun testFindUsageFile1() {
        val testFindUsages = myFixture.testFindUsages("FindUsageFile1.glsl")
        assertEquals(3, testFindUsages.size)
    }

    fun testFindUsageFile2() {
        val testFindUsages = myFixture.testFindUsages("FindUsageFile2.glsl")
        assertEquals(2, testFindUsages.size)
    }

    fun testFindUsageFile3() {
        val testFindUsages = myFixture.testFindUsages("FindUsageFile3.glsl")
        assertEquals(3, testFindUsages.size)
    }

    fun testReference1() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile1.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslSingleDeclaration::class.java)
    }

    fun testReference2() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile2.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslStructDeclarator::class.java)
        assertEquals("structMember", resolve?.text)
    }

    fun testReference3() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile3.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslStructDeclarator::class.java)
        assertEquals("c", resolve?.text)
    }

    fun testReference4() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile4.glsl")
        val resolve = reference?.resolve()
        assertNull(resolve)
    }


    fun testReference5() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile5.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslSingleDeclaration::class.java)
        assertEquals("i", (resolve as GlslSingleDeclaration).name)
        assertInstanceOf(resolve.parent?.parent, GlslIterationStatement::class.java)
    }

    fun testReference6() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile6.glsl")
        val resolve = reference?.resolve()
        assertNull(resolve)
    }

    fun testReference7() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile7.glsl")
        val resolve = reference?.resolve()
        assertNull(resolve)
    }

    fun testReference8() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile8.glsl")
        val resolve = reference?.resolve()
        assertNull(resolve)
    }

    fun testReference9() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile9.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslSingleDeclaration::class.java)
        assertEquals("dummy", (resolve as GlslSingleDeclaration).name)
    }

    fun testReference10() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile10.glsl")
        val resolve = reference?.resolve()
        assertNull(resolve)
    }

    fun testReference11() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile11.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslParameterDeclarator::class.java)
        assertEquals("param1", (resolve as GlslParameterDeclarator).name)
        assertEquals("int", resolve.getAssociatedType()?.name)
    }

    fun testReference12() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile12.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslParameterDeclarator::class.java)
        assertEquals("param1", (resolve as GlslParameterDeclarator).name)
        assertNotNull(resolve.getAssociatedType())
        assertEquals("int", resolve.getAssociatedType()?.name)
    }

    fun testReference13() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile13.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslStructSpecifier::class.java)
        assertEquals("A", (resolve as GlslStructSpecifier).name)
    }

    fun testReference14() {
        myFixture.configureByFile("ReferenceFile14.glsl")
        val parameterDeclarator = myFixture.elementAtCaret as? GlslParameterDeclarator
        val variableType1 = parameterDeclarator?.getAssociatedType()
        val variableType2 = parameterDeclarator?.getAssociatedType()
        assertNotNull(variableType1)
        assertNotNull(variableType2)
        assertEquals("int", variableType1?.name)
        assertEquals("int", variableType2?.name)
    }

    fun testReference15() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile15.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslStructDeclarator::class.java)
        assertEquals("inner", (resolve as GlslStructDeclarator).name)
    }

    fun testReference16() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile16.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslStructDeclarator::class.java)
        assertEquals("vel", (resolve as GlslStructDeclarator).name)
    }

    fun testReference17() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile17.comp")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslStructDeclarator::class.java)
        assertEquals("gl_LocalInvocationID", (resolve as GlslStructDeclarator).name)
    }

    fun testReference18() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile18.comp")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslStructDeclarator::class.java)
        assertEquals("x", (resolve as GlslStructDeclarator).name)
    }

    fun testReference19() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile19.frag")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslStructDeclarator::class.java)
        assertEquals("gl_FragCoord", (resolve as GlslStructDeclarator).name)
    }

    fun testReference20() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile20.frag")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslSingleDeclaration::class.java)
        assertEquals("gl_MaxGeometryInputComponents", (resolve as GlslSingleDeclaration).name)
    }

    fun testReference21() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile21.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslStructDeclarator::class.java)
        assertEquals("xy", (resolve as GlslStructDeclarator).name)
    }

    fun testReference22() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile22.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslStructDeclarator::class.java)
        assertEquals("w", (resolve as GlslStructDeclarator).name)
    }

    fun testReference23() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile23.glsl")
        val resolve = reference?.resolve()
        assertNull(resolve)
    }

    fun testReference24() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile24.glsl")
        val resolve = reference?.resolve()
        assertNull(resolve)
    }

    fun testReference25() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile25.glsl")
        val resolve = reference?.resolve()
        assertNull(resolve)
    }

    fun testReference26() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile26.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslStructDeclarator::class.java)
    }

    fun testReference27() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile27.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslStructDeclarator::class.java)
        assertEquals("xz", (resolve as GlslStructDeclarator).name)
    }

    fun testReference28() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile28.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslSingleDeclaration::class.java)
        assertEquals("arr", (resolve as GlslSingleDeclaration).name)
    }

    fun testReference29() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile29.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslSingleDeclaration::class.java)
        assertEquals("a", (resolve as GlslSingleDeclaration).name)
    }

    fun testReference30() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile30.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslPpDefineObject::class.java)
        assertEquals("PI", (resolve as GlslPpDefineObject).name)
    }

    fun testReference31() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile31.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslPpDefineFunction::class.java)
        assertEquals("f", (resolve as GlslPpDefineFunction).name)
    }

    fun testReference32() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile32.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslPpDefineObject::class.java)
        assertEquals("VAR", (resolve as GlslPpDefineObject).name)
    }

    fun testReference33() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile33.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslPpDefineObject::class.java)
        assertEquals("ZZZ", (resolve as GlslPpDefineObject).name)
    }

    fun testReference34() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile34.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslSingleDeclaration::class.java)
        assertEquals("a", (resolve as GlslSingleDeclaration).name)
    }

    fun testReference35() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile35.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslInitDeclaratorVariable::class.java)
        assertEquals("b", (resolve as GlslInitDeclaratorVariable).name)
    }

    fun testReference36() {
        val reference = myFixture.getReferenceAtCaretPosition("ReferenceFile36.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslInitDeclaratorVariable::class.java)
        assertEquals("c", (resolve as GlslInitDeclaratorVariable).name)
    }
}
