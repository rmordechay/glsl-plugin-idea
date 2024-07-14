import com.intellij.codeInsight.completion.CompletionType
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class GlslCompletionTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String {
        return "src/test/testData/completion"
    }

    fun testCompletion1() {
        myFixture.configureByFiles("CompletionFile1.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        assertEquals(lookupElementStrings?.size, 12)
        assertContainsElements(lookupElementStrings!!, listOf("abs(float x)"))
    }

    fun testCompletion2() {
        myFixture.configureByFiles("CompletionFile2.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        assertNotNull(lookupElementStrings)
        assertContainsElements(lookupElementStrings!!, "max(int x, int y)")
    }

    fun testCompletion3() {
        myFixture.configureByFiles("CompletionFile3.geom")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        assertNotNull(lookupElementStrings)
        assertContainsElements(lookupElementStrings!!, "EmitVertex()")
    }

    fun testCompletion4() {
        myFixture.configureByFiles("CompletionFile4.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        assertNotNull(lookupElementStrings)
        assertContainsElements(lookupElementStrings!!, "while")
    }

    fun testCompletion5() {
        myFixture.configureByFiles("CompletionFile5.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        assertNotNull(lookupElementStrings)
        assertContainsElements(lookupElementStrings!!, "switch")
    }

    fun testCompletion6() {
        myFixture.configureByFiles("CompletionFile6.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        assertNotNull(lookupElementStrings)
        assertDoesntContain(lookupElementStrings!!, "switch")
    }

    fun testCompletion7() {
        myFixture.configureByFiles("CompletionFile7.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        assertNotNull(lookupElementStrings)
        assertDoesntContain(lookupElementStrings!!, "while")
    }

    fun testCompletion8() {
        myFixture.configureByFiles("CompletionFile8.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        assertNotNull(lookupElementStrings)
        assertContainsElements(lookupElementStrings!!, "define")
    }

//    fun testCompletion9() {
//        myFixture.configureByFiles("CompletionFile9.glsl")
//        myFixture.complete(CompletionType.BASIC)
//        val lookupElementStrings = myFixture.lookupElementStrings
//        assertNotNull(lookupElementStrings)
//        assertContainsElements(lookupElementStrings!!, "110", "330", "450", "460")
//    }

    fun testCompletion10() {
        myFixture.configureByFiles("CompletionFile10.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        val expectedComponents = listOf("x", "y", "r", "g", "s", "t")
        assertNotNull(lookupElementStrings)
        assertContainsElements(lookupElementStrings!!.toList(), expectedComponents)
    }

    fun testCompletion11() {
        myFixture.configureByFiles("CompletionFile11.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        val expectedComponents = listOf("b", "g", "r", "rb", "rg", "rgr", "s", "st", "t", "xy", "yz", "z")
        assertNotNull(lookupElementStrings)
        assertContainsElements(lookupElementStrings!!.toList(), expectedComponents)
    }

    fun testCompletion12() {
        myFixture.configureByFiles("CompletionFile12.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        val expectedComponents = listOf("bb", "arbg", "arbr", "arg", "bggr", "argb", "argg", "gb", "arr", "arra", "arrb")
        assertNotNull(lookupElementStrings)
        assertContainsElements(lookupElementStrings!!, expectedComponents)
    }

    fun testCompletion13() {
        myFixture.configureByFiles("CompletionFile13.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        val expectedTypeQualifiers = listOf("in", "inout", "invariant", "subroutine")
        assertNotNull(lookupElementStrings)
        assertContainsElements(lookupElementStrings!!, expectedTypeQualifiers)
    }

    fun testCompletion14() {
        myFixture.configureByFiles("CompletionFile14.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        val expectedTypes = listOf("float", "float16_t", "float32_t", "float64_t")
        assertNotNull(lookupElementStrings)
        assertSameElements(lookupElementStrings!!, expectedTypes)
    }

    fun testCompletion15() {
        myFixture.configureByFiles("CompletionFile15.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        assertNullOrEmpty(lookupElementStrings)
    }

    fun testCompletion16() {
        myFixture.configureByFiles("CompletionFile16.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        val typeQualifiers = listOf("in", "inout", "invariant", "subroutine")
        assertNotNull(lookupElementStrings)
        assertDoesntContain(lookupElementStrings!!, typeQualifiers)
    }

    fun testCompletion17() {
        myFixture.configureByFiles("CompletionFile17.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        assertNotNull(lookupElementStrings)
        assertContainsElements(lookupElementStrings!!, "int", "i8vec4", "inout", "invariant")
    }

    fun testCompletion18() {
        myFixture.configureByFiles("CompletionFile18.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        val expectedTypeQualifiers = listOf("in", "inout", "invariant", "subroutine")
        assertNotNull(lookupElementStrings)
        assertContainsElements(lookupElementStrings!!, expectedTypeQualifiers)
    }

    fun testCompletion19() {
        myFixture.configureByFiles("CompletionFile19.comp")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        assertNotNull(lookupElementStrings)
        assertContainsElements(lookupElementStrings!!, "gl_MaxAtomicCounterBindings", "gl_MaxAtomicCounterBufferSize", "gl_MaxComputeWorkGroupSize")
    }

    fun testCompletion20() {
        myFixture.configureByFiles("CompletionFile20.comp")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        val expectedComponents = listOf("b", "g", "r", "rb", "rg", "rgr", "s", "st", "t", "xy", "yz", "z")
        assertNotNull(lookupElementStrings)
        assertContainsElements(lookupElementStrings!!.toList(), expectedComponents)
    }

    fun testCompletion21() {
        myFixture.configureByFiles("CompletionFile21.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        val expectedComponents = listOf("b", "g", "r", "rb", "rg", "rgr", "s", "st", "t", "xy", "yz", "z")
        assertNotNull(lookupElementStrings)
        assertContainsElements(lookupElementStrings!!.toList(), expectedComponents)
    }

    fun testCompletion22() {
        myFixture.configureByFiles("CompletionFile22.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        val expectedComponents = listOf("vec")
        assertNotNull(lookupElementStrings)
        assertSameElements(lookupElementStrings!!.toList(), expectedComponents)
    }

    fun testCompletion23() {
        myFixture.configureByFiles("CompletionFile23.glsl")
        myFixture.complete(CompletionType.BASIC)
        val lookupElementStrings = myFixture.lookupElementStrings
        assertEquals(0, lookupElementStrings?.size)
    }

    fun testCompletion24() {
        myFixture.configureByFiles("CompletionFile24a.glsl", "include-test/include-test2/CompletionFile24b.glsl", "include-test/include-test3/CompletionFile24c.glsl")
        myFixture.complete(CompletionType.BASIC)
        val expectedComponents = listOf("include-test")
        val lookupElementStrings = myFixture.lookupElementStrings
        assertEquals(1, lookupElementStrings?.size)
        assertSameElements(lookupElementStrings!!.toList(), expectedComponents)
    }
}
