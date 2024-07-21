import com.intellij.testFramework.fixtures.BasePlatformTestCase
import glsl.plugin.inspections.GlslInspectionIncompatibleType
import glsl.plugin.inspections.GlslInspectionTooManyArguments

class GlslDummyTest : BasePlatformTestCase() {
    override fun getTestDataPath(): String {
        return "src/test/testData/dummy"
    }

    fun testDummy() {
//        myFixture.configureByFiles("dummy.glsl")
//        myFixture.enableInspections(GlslInspectionIncompatibleType())
//        myFixture.testHighlighting(false, false, false)
        val reference = myFixture.getReferenceAtCaretPosition("dummy.glsl")
        val resolve = reference?.resolve()
        assertNotNull(resolve)
    }
}