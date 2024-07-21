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

    fun testInspections1() {
        myFixture.configureByFile("InspectionsFile1.glsl")
        myFixture.checkHighlighting(false, false, false)
    }

    fun testInspections2() {
        myFixture.configureByFile("InspectionsFile2.frag")
        myFixture.checkHighlighting(false, false, false)
    }

    fun testInspections3() {
        myFixture.configureByFiles("InspectionsFile3.glsl")
        myFixture.checkHighlighting(false, false, false)
    }

    fun testInspections4() {
        myFixture.configureByFiles("InspectionsFile4.glsl")
        myFixture.checkHighlighting(false, false, false)
    }

    fun testInspections5() {
        myFixture.configureByFiles("InspectionsFile5.glsl")
        myFixture.checkHighlighting(false, false, false)
    }

    fun testInspections6() {
        myFixture.configureByFiles("InspectionsFile6.glsl")
        myFixture.checkHighlighting(false, false, false)
    }
}