import com.intellij.codeInsight.completion.CompletionType
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.Test

class GlslCompletionTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String {
        return "src/test/testData/completion"
    }

    @Test
    fun testCompletionFile1() {
        myFixture.configureByFiles("CompletionFile1.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        assertEquals(lookupElementStrings?.size, 12)
        assertContainsElements(lookupElementStrings!!, listOf("abs(float x)"))
    }

    @Test
    fun testCompletionFile2() {
        myFixture.configureByFiles("CompletionFile2.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        assertEquals(lookupElementStrings?.first().toString(), "main()")
    }

    @Test
    fun testCompletionFile3() {
        myFixture.configureByFiles("CompletionFile3.geom")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        assertNotNull(lookupElementStrings)
        assertContainsElements(lookupElementStrings!!, "EmitVertex()")
    }

    @Test
    fun testCompletionFile4() {
        myFixture.configureByFiles("CompletionFile4.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        assertNotNull(lookupElementStrings)
        assertContainsElements(lookupElementStrings!!, "while")
    }

    @Test
    fun testCompletionFile5() {
        myFixture.configureByFiles("CompletionFile5.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        assertNotNull(lookupElementStrings)
        assertContainsElements(lookupElementStrings!!, "switch")
    }

    @Test
    fun testCompletionFile6() {
        myFixture.configureByFiles("CompletionFile6.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        assertNotNull(lookupElementStrings)
        assertDoesntContain(lookupElementStrings!!, "switch")
    }

    @Test
    fun testCompletionFile7() {
        myFixture.configureByFiles("CompletionFile7.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        assertNotNull(lookupElementStrings)
        assertDoesntContain(lookupElementStrings!!, "while")
    }

    @Test
    fun testCompletionFile8() {
        myFixture.configureByFiles("CompletionFile8.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        assertNotNull(lookupElementStrings)
        assertContainsElements(lookupElementStrings!!, "define")
    }

//    @Test
//    fun testCompletionFile9() {
//        myFixture.configureByFiles("CompletionFile9.glsl")
//        myFixture.complete(CompletionType.BASIC)
//        val lookupElementStrings = myFixture.lookupElementStrings
//        assertNotNull(lookupElementStrings)
//        assertContainsElements(lookupElementStrings!!, "110", "330", "450", "460")
//    }

    @Test
    fun testCompletionFile10() {
        myFixture.configureByFiles("CompletionFile10.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        val expectedComponents = listOf("x", "y", "r", "g", "s", "t")
        assertNotNull(lookupElementStrings)
        assertContainsElements(lookupElementStrings!!.toList(), expectedComponents)
    }

    @Test
    fun testCompletionFile11() {
        myFixture.configureByFiles("CompletionFile11.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        val expectedComponents = listOf("b", "g", "r", "rb", "rg", "rgr", "s", "st", "t", "xy", "yz", "z")
        assertNotNull(lookupElementStrings)
        assertContainsElements(lookupElementStrings!!.toList(), expectedComponents)
    }

    @Test
    fun testCompletionFile12() {
        myFixture.configureByFiles("CompletionFile12.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        val expectedComponents = listOf("bb", "arbg", "arbr", "arg", "bggr", "argb", "argg", "gb", "arr", "arra", "arrb")
        assertNotNull(lookupElementStrings)
        assertContainsElements(lookupElementStrings!!, expectedComponents)
    }

    @Test
    fun testCompletionFile13() {
        myFixture.configureByFiles("CompletionFile13.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        val expectedTypeQualifiers = listOf("in", "inout", "invariant", "subroutine")
        assertNotNull(lookupElementStrings)
        assertContainsElements(lookupElementStrings!!, expectedTypeQualifiers)
    }

    @Test
    fun testCompletionFile14() {
        myFixture.configureByFiles("CompletionFile14.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        val expectedTypes = listOf("float", "float16_t", "float32_t", "float64_t")
        assertNotNull(lookupElementStrings)
        assertSameElements(lookupElementStrings!!, expectedTypes)
    }

    @Test
    fun testCompletionFile15() {
        myFixture.configureByFiles("CompletionFile15.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        assertNullOrEmpty(lookupElementStrings)
    }

    @Test
    fun testCompletionFile16() {
        myFixture.configureByFiles("CompletionFile16.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        val typeQualifiers = listOf("in", "inout", "invariant", "subroutine")
        assertNotNull(lookupElementStrings)
        assertDoesntContain(lookupElementStrings!!, typeQualifiers)
    }

    @Test
    fun testCompletionFile17() {
        myFixture.configureByFiles("CompletionFile17.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        assertNotNull(lookupElementStrings)
        assertContainsElements(lookupElementStrings!!, "int", "i8vec4", "inout", "invariant")
    }

    @Test
    fun testCompletionFile18() {
        myFixture.configureByFiles("CompletionFile18.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        val expectedTypeQualifiers = listOf("in", "inout", "invariant", "subroutine")
        assertNotNull(lookupElementStrings)
        assertContainsElements(lookupElementStrings!!, expectedTypeQualifiers)
    }

    @Test
    fun testCompletionFile19() {
        myFixture.configureByFiles("CompletionFile19.comp")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        assertNotNull(lookupElementStrings)
        assertContainsElements(lookupElementStrings!!, "gl_MaxAtomicCounterBindings", "gl_MaxAtomicCounterBufferSize", "gl_MaxComputeWorkGroupSize")
    }

    @Test
    fun testCompletionFile20() {
        myFixture.configureByFiles("CompletionFile20.comp")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        val expectedComponents = listOf("b", "g", "r", "rb", "rg", "rgr", "s", "st", "t", "xy", "yz", "z")
        assertNotNull(lookupElementStrings)
        assertContainsElements(lookupElementStrings!!.toList(), expectedComponents)
    }

    @Test
    fun testCompletionFile21() {
        myFixture.configureByFiles("CompletionFile21.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        val expectedComponents = listOf("b", "g", "r", "rb", "rg", "rgr", "s", "st", "t", "xy", "yz", "z")
        assertNotNull(lookupElementStrings)
        assertContainsElements(lookupElementStrings!!.toList(), expectedComponents)
    }

    @Test
    fun testCompletionFile22() {
        myFixture.configureByFiles("CompletionFile22.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        val expectedComponents = listOf("vec")
        assertNotNull(lookupElementStrings)
        assertSameElements(lookupElementStrings!!.toList(), expectedComponents)
    }

    @Test
    fun testCompletionFile23() {
        myFixture.configureByFiles("CompletionFile23.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        println(lookupElementStrings)
        assertEquals(0, lookupElementStrings?.size)
    }
}
