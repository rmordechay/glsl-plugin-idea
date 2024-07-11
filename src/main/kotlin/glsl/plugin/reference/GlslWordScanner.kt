package glsl.plugin.reference

import com.intellij.lang.cacheBuilder.WordOccurrence
import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.util.Processor
import glsl.data.GlslTokenSets
import glsl.plugin.language.GlslLexer

/**
 *
 */
class GlslWordScanner: WordsScanner {
    @Volatile
    private var myBusy = false
    private val lexer = GlslLexer()

    /**
     *
     */
    override fun processWords(fileText: CharSequence, processor: Processor<in WordOccurrence>) {
        if (myBusy) return
        try {
            lexer.start(fileText)
            while (true) {
                val type = lexer.tokenType ?: return
                if (GlslTokenSets.IDENTIFIERS.contains(type)) {
                    val wordOccurrence =
                        WordOccurrence(fileText, lexer.tokenStart, lexer.tokenEnd, WordOccurrence.Kind.CODE)
                    processor.process(wordOccurrence)
                }
                lexer.advance()
            }
        } finally {
            myBusy = false
        }
    }
}