import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.Test

class GlslDummyTest : BasePlatformTestCase() {
    override fun getTestDataPath(): String {
        return "src/test/testData/dummy"
    }

    fun testDummy() {
        myFixture.configureByFiles("dummy.glsl")
        myFixture.checkHighlighting(false, false, false)
//        val referenceAtCaretPosition = myFixture.getReferenceAtCaretPosition("dummy.glsl")?.resolve()
    }
}