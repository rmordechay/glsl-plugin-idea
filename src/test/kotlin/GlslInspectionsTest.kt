import com.intellij.testFramework.fixtures.BasePlatformTestCase
import glsl.plugin.inspections.*

class GlslInspectionsTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(
            GlslInspectionTooFewArguments(),
            GlslInspectionTooManyArguments(),
            GlslInspectionIncompatibleType(),
            GlslInspectionMissingReturn(),
            GlslInspectionNoMatchingFunction(),
            GlslInspectionOperatorDoesNotOperate(),
        )
    }

    override fun getTestDataPath(): String {
        return "src/test/testData/inspections"
    }

    fun testAnnotator1() {
        myFixture.configureByFile("InspectionsFile1.glsl")
        myFixture.checkHighlighting(false, false, false)
    }

    fun testAnnotator2() {
        myFixture.configureByFile("InspectionsFile2.frag")
        myFixture.checkHighlighting(false, false, false)
    }

    fun testAnnotator3() {
        myFixture.configureByFiles("InspectionsFile3.glsl")
        myFixture.checkHighlighting(false, false, false)
    }

    fun testAnnotator4() {
        myFixture.configureByFiles("InspectionsFile4.glsl")
        myFixture.checkHighlighting(false, false, false)
    }

    fun testAnnotator5() {
        myFixture.configureByFiles("InspectionsFile5.glsl")
        myFixture.checkHighlighting(false, false, false)
    }

    fun testAnnotator6() {
        myFixture.configureByFiles("InspectionsFile6.glsl")
        myFixture.checkHighlighting(false, false, false)
    }
}