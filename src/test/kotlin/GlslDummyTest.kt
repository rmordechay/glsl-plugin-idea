import com.intellij.testFramework.fixtures.BasePlatformTestCase
import junit.framework.TestCase
import junit.framework.TestCase.assertNull
import org.junit.Test

class GlslDummyTest : BasePlatformTestCase() {
    override fun getTestDataPath(): String {
        return "src/test/testData/dummy"
    }

    fun testDummy() {
//        myFixture.configureByFiles("dummy.glsl")
//        myFixture.checkHighlighting(false, false, false)
        val reference = myFixture.getReferenceAtCaretPosition("dummy.glsl")
        val resolve = reference?.resolve()
        assertNotNull(resolve)
    }
}