import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.Test

class GlslAnnotatorTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String {
        return "src/test/testData/annotator"
    }

    @Test
    fun testAnnotatorFile1() {
        myFixture.configureByFile("AnnotatorFile1.glsl")
        myFixture.checkHighlighting(false, true, false)
    }

    @Test
    fun testAnnotatorFile2() {
        myFixture.configureByFile("AnnotatorFile2.frag")
        myFixture.checkHighlighting(false, true, false)
    }

    @Test
    fun testAnnotatorFile3() {
        myFixture.configureByFile("AnnotatorFile3.glsl")
        myFixture.checkHighlighting(false, false, false)
    }

    @Test
    fun testAnnotatorFile4() {
        myFixture.configureByFile("AnnotatorFile4.glsl")
        myFixture.checkHighlighting(false, false, false)
    }

    @Test
    fun testAnnotatorFile5() {
        myFixture.configureByFile("AnnotatorFile5.glsl")
        myFixture.checkHighlighting(false, false, false)
    }

    @Test
    fun testAnnotatorFile6() {
        myFixture.configureByFile("AnnotatorFile6.glsl")
        myFixture.checkHighlighting(false, false, false)
    }

    @Test
    fun testAnnotatorFile7() {
        myFixture.configureByFile("AnnotatorFile7.glsl")
        myFixture.checkHighlighting(false, false, false)
    }

    @Test
    fun testAnnotatorFile8() {
        myFixture.configureByFile("AnnotatorFile8.glsl")
        myFixture.checkHighlighting(false, false, false)
    }
}