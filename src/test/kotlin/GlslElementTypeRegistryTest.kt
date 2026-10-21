import com.intellij.psi.PsiFileFactory
import com.intellij.psi.tree.IElementType
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import glsl.plugin.language.GlslFileType

/**
 * IntelliJ registers every [IElementType] ever constructed in a global registry that holds at most
 * [Short.MAX_VALUE] entries; once it's full, the IDE can no longer parse anything. Element types must therefore be
 * created once, never per file or per parse.
 */
class GlslElementTypeRegistryTest : BasePlatformTestCase() {

    fun testParsingGlslFilesDoesNotRegisterNewElementTypes() {
        // Parse once first, so element types that are legitimately created lazily on first use already exist.
        parseGlslFile(0)
        val registeredBefore = IElementType.getAllocatedTypesCount()

        repeat(50) { parseGlslFile(it + 1) }

        assertEquals(
            "expected parsing GLSL files to reuse the existing element types instead of registering new ones",
            registeredBefore,
            IElementType.getAllocatedTypesCount()
        )
    }

    /**
     * Creates a GLSL file and forces it to be fully parsed, like opening or indexing it does.
     */
    private fun parseGlslFile(index: Int) {
        val file = PsiFileFactory.getInstance(project)
            .createFileFromText("file$index.glsl", GlslFileType, "void main() { float x$index = abs(1.0); }")
        assertNotNull("expected the GLSL file to have a parsed syntax tree", file.node.firstChildNode)
    }
}
