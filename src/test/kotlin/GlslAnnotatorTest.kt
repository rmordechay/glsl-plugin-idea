import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.Test

class GlslAnnotatorTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String {
        return "src/test/testData/annotator"
    }

    fun testAnnotatorFile1() {
        myFixture.configureByFile("AnnotatorFile1.glsl")
        myFixture.checkHighlighting(false, true, false)
    }

    fun testAnnotatorFile2() {
        myFixture.configureByFile("AnnotatorFile2.frag")
        myFixture.checkHighlighting(false, true, false)
    }

    @Test
    fun testAnnotatorFile3() {
        myFixture.configureByFiles("AnnotatorFile3.glsl")
        myFixture.checkHighlighting(false, false, false)
    }
}