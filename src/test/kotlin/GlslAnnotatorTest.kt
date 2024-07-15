import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.Test

class GlslAnnotatorTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String {
        return "src/test/testData/annotator"
    }

    fun testAnnotator1() {
        myFixture.configureByFile("AnnotatorFile1.glsl")
        myFixture.checkHighlighting(false, true, false)
    }

    fun testAnnotator2() {
        myFixture.configureByFile("AnnotatorFile2.frag")
        myFixture.checkHighlighting(false, true, false)
    }

    fun testAnnotator3() {
        myFixture.configureByFiles("AnnotatorFile3.glsl")
        myFixture.checkHighlighting(false, true, false)
    }

    fun testAnnotator4() {
        myFixture.configureByFiles("AnnotatorFile4.glsl")
        myFixture.checkHighlighting(false, false, false)
    }

    fun testAnnotator5() {
        myFixture.configureByFiles("AnnotatorFile5.glsl")
        myFixture.checkHighlighting(false, false, false)
    }

    fun testAnnotator6() {
        myFixture.configureByFiles("AnnotatorFile6.glsl")
        myFixture.checkHighlighting(false, false, false)
    }
}