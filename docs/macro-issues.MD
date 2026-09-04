# Macro function-call parsing bug

## Symptom

`GlslParserTest.testParserFile` fails (`src/test/testData/parser/ParserFile.txt` vs
actual parse tree) because a function-like macro call used as part of an expression,
with a real argument list, does not get wrapped the same way a plain function call
does. Minimal repro:

```glsl
#define f2(a, b) a + b
int dummy = f2(g(1, c), h(2, 3));
```

## Current (buggy) tree structure

For `f2(g(1, c), h(2, 3))` (source offsets 246-266), the parser currently produces:

```
GlslVariableIdentifierImpl(VARIABLE_IDENTIFIER)(246,248)
  PsiElement(MACRO_FUNCTION_CALL)('f2')(246,248)
GlslMacroFuncCallParamsBlockImpl(MACRO_FUNC_CALL_PARAMS_BLOCK)(248,266)
  PsiElement(LEFT_PAREN_MACRO_CALL)('(')(248,249)
  GlslVariableIdentifierImpl(VARIABLE_IDENTIFIER)(249,250)
    PsiElement(IDENTIFIER)('g')(249,250)
  PsiElement(()('(')(250,251)
  PsiElement(INTCONSTANT)('1')(251,252)
  PsiElement(,)(',')(252,253)
  GlslVariableIdentifierImpl(VARIABLE_IDENTIFIER)(254,255)
    PsiElement(IDENTIFIER)('c')(254,255)
  PsiElement())(')')(255,256)
  PsiElement(,)(',')(256,257)
  GlslVariableIdentifierImpl(VARIABLE_IDENTIFIER)(258,259)
    PsiElement(IDENTIFIER)('h')(258,259)
  PsiElement(()('(')(259,260)
  PsiElement(INTCONSTANT)('2')(260,261)
  PsiElement(,)(',')(261,262)
  PsiElement(INTCONSTANT)('3')(263,264)
  PsiElement())(')')(264,265)
  PsiElement(RIGHT_PAREN_MACRO_CALL)(')')(265,266)
GlslVariableIdentifierImpl(VARIABLE_IDENTIFIER)(266,266)
  <empty list>
```

The `VARIABLE_IDENTIFIER` (name) and `MACRO_FUNC_CALL_PARAMS_BLOCK` (args) end up as
flat siblings instead of being nested under one node representing "the call", and an
extra empty, zero-width `VARIABLE_IDENTIFIER(266,266)` is tacked on afterward
(a symptom of the root cause below, see the raw lexer dump).

For comparison, `f(1, 2)` (a macro call with trivial single-token arguments, offsets
201-208) parses *correctly* today, producing:

```
GlslAddExprImpl(ADD_EXPR)(201,208)
  GlslUnaryExprImpl(UNARY_EXPR)(201,208)
    GlslPrimaryExprImpl(PRIMARY_EXPR)(201,208)
      GlslVariableIdentifierImpl(VARIABLE_IDENTIFIER)(201,202)
        PsiElement(MACRO_FUNCTION_CALL)('f')(201,202)
      GlslMacroFuncCallParamsBlockImpl(MACRO_FUNC_CALL_PARAMS_BLOCK)(202,208)
        ...
  GlslUnaryExprImpl(UNARY_EXPR)(208,208)
    GlslPrimaryExprImpl(PRIMARY_EXPR)(208,208)
      <empty list>
```

Both of these "correct" and "incorrect" shapes are themselves accidents of how many
times IntelliJ's generated-parser backtracking retries a failed match before giving
up (see below) — `f(1, 2)`'s golden shape includes a trailing empty `UNARY_EXPR`
placeholder and an `ADD_EXPR` wrapper that only exist because retry #21 happens to
land in a spot that looks reasonable. `f2(...)`'s case simply runs out of garbage
tokens/retries sooner and lands somewhere worse. Neither is really a "designed" tree
shape; both are downstream of the same lexer bug.

## Root cause

`GlslLexer.kt` treats a macro function-call as two things at once: the literal
call-site tokens (name, `(`, args, `)`), **and**, immediately afterward, an automatic
expansion of the macro's *body* with the call-site arguments substituted in (used
elsewhere for type inference, e.g. `GlslExprType`). That expansion is driven by
`expandMacro()` / `expandMacroParam()` / `addMacroParamToken()`
(`src/main/kotlin/glsl/plugin/language/GlslLexer.kt:218-291`), triggered from
`advance()` (same file, `:76-103`) as soon as `macroExpansion` is non-null.

The bug: the synthetic expansion tokens have no real source range (there's nothing
"there" to point at), but `getTokenEnd()` and `getTokenText()`
(`GlslLexer.kt:124-133`) don't know that — they blindly return the *raw* underlying
flex lexer's last real token boundaries/text:

```kotlin
override fun getTokenEnd(): Int {
    return lexer.tokenEnd       // frozen at the last real token's end
}

override fun getTokenText(): String {
    return lexer.yytext().toString()   // frozen at the last real token's text
}
```

So every synthetic token after a macro call reports the exact same zero-width
`(offset, offset)` span, with the *same leftover text* as the real closing
delimiter. Raw lexer dump for `f2(g(1, c), h(2, 3))`, obtained by instantiating
`GlslLexer` directly and driving it with `lexer.start(text, 0, 270); while
(lexer.tokenType != null) { ...print tokenStart/tokenEnd/tokenType/tokenText...;
lexer.advance() }` over the snippet above:

```
265-266 RIGHT_PAREN_MACRO_CALL ')'
266-266 IDENTIFIER             ')'
266-266 (                      ')'
266-266 INTCONSTANT            ')'
266-266 ,                      ')'
266-266 WHITE_SPACE            ')'
266-266 IDENTIFIER             ')'
266-266 )                      ')'
266-266 +                      ')'
266-266 WHITE_SPACE            ')'
266-266 IDENTIFIER             ')'
266-266 (                      ')'
266-266 INTCONSTANT            ')'
266-266 ,                      ')'
266-266 WHITE_SPACE            ')'
266-266 INTCONSTANT            ')'
266-266 )                      ')'
266-267 ;                      ';'
```

17 bogus zero-width tokens (all reporting text `')'`) sit between the real call and
the real `;`. `f(1, 2)`'s simpler, single-token arguments produce only 4 such tokens
— which is *why* `f` "works" today and `f2` doesn't: it's purely a matter of how many
garbage tokens IntelliJ's backtracking parser has to chew through before something
downstream commits via pinned error recovery, not a real semantic difference.

`GlslPsiBuilder.macroCall()` (`src/main/kotlin/glsl/plugin/psi/GlslPsiBuilder.kt:35-64`)
manually drives the lexer through the real call tokens (name, `(`, args, `)`) and
closes `MACRO_FUNC_CALL_PARAMS_BLOCK` right after consuming `RIGHT_PAREN_MACRO_CALL`
— but it never accounts for the automatic expansion tokens the lexer keeps emitting
immediately afterward. Those leak straight into the generated parser
(`_GlslParser`), which — being a plain backtracking recursive-descent parser with no
idea these tokens are fake — tries to make sense of them, retries repeatedly, and
whatever pinned-error-recovery rule happens to give up first determines the final
(differently-broken, in each case) tree shape.

Separately, `GlslPsiUtils.primaryExprVariable()`
(`src/main/kotlin/glsl/plugin/psi/GlslPsiUtils.kt:18-28`) is the external rule behind
`primary_expr_variable { elementType=variable_identifier }`
(`grammar/GlslGrammar.bnf:757-759`), which is what *should* wrap a whole macro call
(name + params block) in one outer `VARIABLE_IDENTIFIER` node when it appears in
expression position. But it only checks for a literal `IDENTIFIER` token:

```kotlin
fun primaryExprVariable(builder: PsiBuilder, level: Int): Boolean {
    if (!recursion_guard_(builder, level, "primary_expr_variable")) return false
    val isCurrentTokenIdentifier = builder.tokenType == IDENTIFIER
    ...
}
```

`builder.tokenType` here is itself what triggers `GlslPsiBuilder.macroCall()`'s side
effect (peeking the token type is what converts `MACRO_FUNCTION_CALL` into PSI nodes
in the first place — see `GlslPsiBuilder.getTokenType()`), so by the time this check
runs, the macro call has *already* been fully consumed and turned into sibling nodes,
and whatever token comes next is essentially never `IDENTIFIER`. So this rule always
reports failure for a macro call, even though the call was successfully parsed as a
side effect — which is why the outer wrapping node never gets created.

## Where the fix needs to land

Fixing this properly needs changes in (at least) two places, together:

1. **`src/main/kotlin/glsl/plugin/psi/GlslPsiBuilder.kt`** — after consuming a macro
   call's terminal token (`RIGHT_PAREN_MACRO_CALL` for function calls, the call token
   itself for object calls), the automatic macro-body-expansion tokens the lexer
   emits next need to be consumed/skipped before control returns to the generated
   parser, so they never become visible as real tokens.
2. **`src/main/kotlin/glsl/plugin/psi/GlslPsiUtils.kt`** — `primaryExprVariable()`
   needs to recognize "a macro call was just consumed as a side effect of this very
   token peek" and report success, instead of only matching a literal `IDENTIFIER`.

Neither change is sufficient on its own:

- Skipping the expansion tokens in `GlslPsiBuilder` (1) without also fixing
  `primaryExprVariable` (2) still leaves `primary_expr_variable` reporting failure
  for the macro call, so the outer `VARIABLE_IDENTIFIER` wrapper still never gets
  created — the tree is merely missing the trailing garbage instead of missing the
  wrapper *and* full of garbage.
- Fixing `primaryExprVariable` (2) without skipping the expansion tokens (1) makes
  `primary_expr_variable` claim success immediately (since the token position moved
  during the peek), which short-circuits the backtracking retries that `f(1, 2)`
  currently depends on to reach its "working" shape — so previously-passing macro
  calls (including the simple `f(1, 2)` case) start failing instead, because the
  leaked garbage tokens are still sitting there for the *next* rule to trip over.

Both changes together were prototyped and did make `GlslParserTest` pass (after
regenerating its golden file to match the new, simpler tree shape) — but see the
formatter breakage below, which is why that combination was not kept.

The real, non-hacky fix almost certainly belongs in **`GlslLexer.kt`** itself: the
macro-body expansion feature needs to either carry a distinct, filterable token type
(instead of reusing real token types like `IDENTIFIER`/`INTCONSTANT`/`+`), or not be
interleaved into the same token stream the parser consumes at all (e.g. only used by
whatever consumer — `GlslExprType`? — actually wants macro-body substitution for type
inference, driven separately). Patching around it at the `GlslPsiBuilder`/parser
level (as attempted) is fragile and has proven to have side effects beyond the
targeted macro-call sites (see below).

## Where the formatter breaks

`GlslFormatterTest.testFormatter` reformats `src/test/testData/formatter/formatterFile.glsl`
and diffs against `formatterFileExpected.glsl`. With *both* fixes above applied, this
test goes from passing to badly failing — not just at macro-call sites, but for
**the entire rest of the file** after the first affected macro use: indentation
disappears, and inconsistent single/double spacing appears throughout otherwise
untouched code (loops, switch statements, struct declarations, etc. all lose their
indentation).

The formatter is sensitive to *exact* PSI node shape at both of these files:

- **`src/main/kotlin/glsl/plugin/editor/formatter/GlslBlock.kt`** —
  `getChildAttributes()` (`:56-61`) and `getChildBlock()` (`:83-108`) decide
  indentation by checking `node.elementType`/`childElementType` against specific
  literal sets, e.g. `childElementType in listOf(STATEMENT, STRUCT_DECLARATION,
  PARAMETER_DECLARATOR)` and `COMPOUND_STATEMENTS` (`:12-17`). If a macro call
  doesn't end up wrapped in the expected node type (`STATEMENT`, `EXTERNAL_DECLARATION`,
  etc. — exactly the kind of node the buggy/fixed macro-call parsing changes), these
  checks silently stop matching and blocks fall back to `Indent.getNoneIndent()`.
- **`src/main/kotlin/glsl/plugin/editor/formatter/GlslFormattingModelBuilder.kt`** —
  `createSpaceBuilder()` (`:42-101`) is a big table of spacing rules keyed on
  adjacent `elementType`s (e.g. `.between(DASH, PRIMARY_EXPR).none()`,
  `.between(VARIABLE_IDENTIFIER, LEFT_PAREN).none()`). When the tree shape around a
  macro call shifts (extra/missing `PRIMARY_EXPR`/`ADD_EXPR`/`FUNCTION_CALL` wrapper
  nodes, as seen above), these adjacency rules stop matching for that subtree and its
  neighbors.

Because IntelliJ's formatting model computes indent/alignment for a block relative to
its parent and preceding siblings, one block that unexpectedly falls back to "no
indent" throws off the computed positions for every following sibling block in the
same file — which is why the breakage cascades through the whole rest of
`formatterFile.glsl` instead of staying localized to the handful of macro-call sites
that actually changed shape.

## Recommendation

Don't patch this at the `GlslPsiBuilder`/`GlslPsiUtils` layer. Fix the lexer so macro
body-expansion tokens are never exposed to the parser's normal token stream in the
first place (or are trivially, unambiguously distinguishable so `GlslPsiBuilder` can
filter them without any of the width/text heuristics attempted so far), then verify
against both `GlslParserTest` and `GlslFormatterTest` (and ideally a broader corpus of
real macro-heavy shader files) before considering this fixed.
