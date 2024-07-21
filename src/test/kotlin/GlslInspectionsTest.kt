import com.intellij.testFramework.fixtures.BasePlatformTestCase
import glsl.plugin.inspections.GlslInspectionIncompatibleType
import glsl.plugin.inspections.GlslInspectionMissingReturn
import glsl.plugin.inspections.GlslInspectionOperatorDoesNotOperate

class GlslInspectionsTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String {
        return "src/test/testData/inspections"
    }

    fun testDoesNotOperate() {
        myFixture.enableInspections(GlslInspectionOperatorDoesNotOperate())
        myFixture.configureByFiles("InspectionsTestDoesNotOperate.glsl")
        myFixture.checkHighlighting(false, false, false)
    }

    fun testIncompatibleTypes() {
        myFixture.enableInspections(GlslInspectionIncompatibleType())
        myFixture.configureByFiles("InspectionsTestIncompatibleTypes.glsl")
        myFixture.checkHighlighting(false, false, false)
    }

    fun testMissingReturn() {
        myFixture.enableInspections(GlslInspectionMissingReturn())
        myFixture.configureByFiles("InspectionsTestMissingReturn.glsl")
        myFixture.checkHighlighting(false, false, false)
    }
}