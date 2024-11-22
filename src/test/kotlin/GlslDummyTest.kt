import com.intellij.testFramework.fixtures.BasePlatformTestCase
import glsl.plugin.inspections.*

class GlslDummyTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(
            GlslInspectionTooFewArguments(),
            GlslInspectionTooManyArguments(),
            GlslInspectionIncompatibleType(),
            GlslInspectionMissingReturn(),
//            GlslInspectionNoMatchingFunction(),
            GlslInspectionOperatorDoesNotOperate(),
        )
    }

    override fun getTestDataPath(): String {
        return "src/test/testData/dummy"
    }

    fun testDummy() {
        myFixture.configureByFiles("dummy.glsl")
        myFixture.testHighlighting(false, false, false)
//        val reference = myFixture.getReferenceAtCaretPosition("dummy.glsl")
//        val resolve = reference?.resolve()
//        assertInstanceOf(resolve, GlslSingleDeclaration::class.java)
//        assertEquals("dummy", (resolve as GlslSingleDeclaration).name)
    }
}