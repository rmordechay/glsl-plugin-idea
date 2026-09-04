import com.intellij.testFramework.fixtures.BasePlatformTestCase
import glsl.psi.interfaces.GlslSingleDeclaration
import glsl.psi.interfaces.GlslStructSpecifier

class GlslIncludeTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String {
        return "src/test/testData/include"
    }

    fun testInclude1() {
        myFixture.configureByFiles("IncludeFile2.glsl")
        val reference = myFixture.getReferenceAtCaretPosition("IncludeFile1.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslSingleDeclaration::class.java)
        assertEquals("b", (resolve as GlslSingleDeclaration).name)
    }

    fun testImportCyclesDoNotThrowErrors() {
        myFixture.configureByFile("IncludeFile3.glsl")
        myFixture.checkHighlighting()
        val reference = myFixture.getReferenceAtCaretPosition("IncludeFile3.glsl")
        assertNoThrowable { reference?.resolve() }
    }

    fun testImportCyclesDontThrowErrorsNested() {
        val reference = myFixture.getReferenceAtCaretPosition("IncludeFile6.glsl")
        assertNoThrowable { reference?.resolve() }
    }

    fun testInclude4() {
        myFixture.configureByFiles("shaders/shaders2/IncludeFile7.glsl", "shaders/IncludeFile7.glsl")
        val reference = myFixture.getReferenceAtCaretPosition("shaders/IncludeFile8.glsl")
        val resolve = reference?.resolve()
        assertNull(resolve)
    }

    fun testInclude5() {
        myFixture.configureByFiles("shaders/shaders2/IncludeFile7.glsl", "shaders/IncludeFile7.glsl")
        val reference = myFixture.getReferenceAtCaretPosition("shaders/IncludeFile9.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslSingleDeclaration::class.java)
        assertEquals("a", (resolve as GlslSingleDeclaration).name)
    }

    fun testInclude6() {
        myFixture.configureByFiles("shaders/shaders2/IncludeFile7.glsl", "shaders/IncludeFile7.glsl")
        val reference = myFixture.getReferenceAtCaretPosition("shaders/IncludeFile10.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslSingleDeclaration::class.java)
        assertEquals("b", (resolve as GlslSingleDeclaration).name)
    }

    fun testInclude7() {
        myFixture.configureByFiles("IncludeFile8.glsl")
        val reference = myFixture.getReferenceAtCaretPosition("IncludeFile7.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslStructSpecifier::class.java)
        assertEquals("A", (resolve as GlslStructSpecifier).name)
    }

    fun testRootMarkerFile() {
        myFixture.configureByFiles("root/var_a.glsl", "root/.glsl_idea_root")
        val reference = myFixture.getReferenceAtCaretPosition("root/subfolder/test_root_marker.glsl")
        val resolve = reference?.resolve()
        assertInstanceOf(resolve, GlslSingleDeclaration::class.java)
        assertEquals("a", (resolve as GlslSingleDeclaration).name)
    }
}
