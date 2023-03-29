package glsl.plugin.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet


class GlslFileReferenceSet(path: String, element: PsiElement, provider: PsiReferenceProvider?) :
    FileReferenceSet(path, element, 0, provider, true) {
    override fun createFileReference(range: TextRange?, index: Int, text: String): FileReference? {
        if (range == null) return null
        val rangeShiftedRight = range.shiftRight(1) // Shifted one right because of parentheses or brackets
        return FileReference(this, rangeShiftedRight, index, text)
    }
//    inner class GlslFileReference(fileReferenceSet: FileReferenceSet, range: TextRange?, index: Int, path: String) : FileReference(fileReferenceSet, range, index, path)
}