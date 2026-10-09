# Coding Conventions

> **Status: draft.** These conventions are partly derived from what the codebase already does and partly
> new rules. Where existing code doesn't follow a rule, see [Existing code](#existing-code).

The key words **must**, **should** and **may** are used in their usual RFC 2119 sense.

## Project layout

| Path                                    | Contents                                                                                            |
|-----------------------------------------|-----------------------------------------------------------------------------------------------------|
| `grammar/`                              | Grammar-Kit sources: `GlslGrammar.bnf`, `GlslLexer.flex`, `GlslHighlightLexer.flex`                 |
| `src/main/kotlin/glsl/data/`            | Static language data: token sets, definitions, builtin docs                                         |
| `src/main/kotlin/glsl/plugin/<feature>` | Plugin features: `completion`, `editor`, `inspections`, `language`, `preview`, `psi`, `reference`   |
| `src/main/kotlin/glsl/plugin/utils/`    | Shared helpers and plugin exception types (`utils/exceptions`)                                      |
| `src/main/resources/`                   | `plugin.xml`, builtin GLSL objects, icons, templates, color schemes                                 |
| `src/test/kotlin/`                      | Tests (default package), one `<Subject>Test` class per feature area                                 |
| `src/test/testData/<area>/`             | Fixture files for the matching test class                                                           |

- New code **must** be written in Kotlin. The Java that exists (`utils/swing`, `VectorStructGen`) is legacy.
- The lexer and parser (`glsl.GlslTypes`, `glsl.psi.*`) are generated into `build/generated/sources/grammarkit`.
  Never edit generated code. Change the `.bnf`/`.flex` file and regenerate with `./gradlew generateGrammarClean`.
- Put new code in the feature package it belongs to rather than in `utils`. Only add to `utils` when the code is
  genuinely shared across features.

## Formatting

- Follow the Kotlin official code style (`kotlin.code.style=official` in `gradle.properties`): 4-space indent,
  and reformat with the IDE before committing.
- No trailing semicolons.
- Use explicit imports. Wildcard imports **may** be used for the generated packages (`glsl.GlslTypes.*`,
  `glsl.psi.interfaces.*`) and for static OpenGL bindings (`org.lwjgl.opengl.GL20.*`), where explicit lists
  get unwieldy.

## Naming

- Prefix plugin classes with `Glsl` (`GlslCompletionContributor`, `GlslReference`). PSI mixins that provide
  names live in `psi/named` and are prefixed `GlslNamed`. Shader-preview classes use `Shader`/`Gl` prefixes.
- Constants are `SCREAMING_SNAKE_CASE` and declared `const val` where possible: top-level `private` if they are
  file-local, or in a `companion object` if they belong to a class.
- Name things after what they mean, not how they're built: `uTimeLocation`, not `loc1`.

### Function names

Function names start with a verb, and the verb **must** tell the caller what calling the function costs and
what it does:

| Prefix                          | Use for                                                                                  |
|---------------------------------|------------------------------------------------------------------------------------------|
| `get…` / property               | Cheap reads with no side effects. Nothing is computed, searched, allocated or changed.  |
| `compute…` / `resolve…` / `infer…` | Values derived by non-trivial work, such as type inference or reference resolution. |
| `find…` / `search…`             | Lookups in an index, the file system or the PSI tree.                                   |
| `create…` / `load…` / `compile…` | Work that allocates or builds something, such as GL objects, files or lookup elements. |
| `collect…`                      | Functions that add results to shared state instead of returning them.                   |
| `is…` / `has…` / `can…` / `should…` | Boolean results.                                                                    |

- `find…` and `resolve…` functions **must** return their result: a nullable value for one, a collection for
  many. If a function only collects into shared state, name it `collect…`.
- A singular name returns one thing and a plural name returns a collection: `findImportFile(): PsiFile?`,
  `findImportFiles(): List<PsiFile>`.
- Use the same name for the same concept everywhere. Don't call it `getUniforms` in one class and
  `getUniformMappings` in another.
- Paired operations use matching verbs: `acquire`/`release`, `start`/`stop`, `add`/`remove`.
- If a function's control flow isn't obvious from its name and signature, its docblock **must** say so. For
  example, document that a function throws to abort an enclosing lookup.

**Exception:** functions in `GlslPsiUtils` that `GlslGrammar.bnf` references as external rules (`<<ppText>>`,
`<<macroBodyToken>>`, …) follow grammar-rule naming and must match the `.bnf`. Don't rename them on their own.

### Acronyms and abbreviations

- Camel-case acronyms like ordinary words: `GlProcessHandler`, `GlContext`, not `GLProcessHandler`. The exception is
  `MC`, which is always written in capitals (see the [glossary](GLOSSARY.md)).
- Don't abbreviate, except for the terms in [GLOSSARY.md](GLOSSARY.md). Write `fragmentShader`, `vertexShader`,
  `function`, `declaration`, `attribute` and `string`, not `fragShader`, `vShader`, `func`, `decl`, `attr` or `str`.
  Single-letter or short local variables in small scopes (`i`, `it`, `vf`) are fine.
- The glossary's grammar terms (`Pp`, `Expr`, …) appear in the generated PSI classes. Hand-written code **may**
  use them where it refers to the corresponding grammar or PSI concept, so names stay searchable across the two.
- Only domain acronyms may be added to the glossary, not grammar terms.

### Grammar names

The grammar abbreviations are legacy. New grammar rules and tokens (`.bnf` and `.flex`) **must not** introduce
new abbreviations. Spell names out (`function_header`, not `func_header`), because grammar names propagate into
the generated PSI classes and from there into hand-written code. The one exception: a new rule that belongs to an
existing family keeps that family's current prefix, abbreviated or not. A new preprocessor directive is
`pp_warning`, not `preprocessor_warning`, so that related names stay together.

## Documentation

### Public methods

Every public method (and public property with non-obvious behavior) **must** have a KDoc block written for the
*consumer* of the function. It should answer:

- **Who should call this, and why?** What problem it solves for the caller, and when to use it instead of an
  alternative.
- **What does the caller need to know?** Preconditions (e.g. must run in a read action, needs a current GL
  context), what `null` or an empty result means, side effects, and exceptions thrown.

Describe the contract, not the implementation. Use `@param`/`@return`/`@throws` only when they add information
beyond the signature. Link related symbols with `[Name]`.

```kotlin
/**
 * Compiles [fragmentShaderSource] against the plugin's default full-screen vertex shader and links it into a program
 * ready for rendering. Used by the preview whenever a shader is (re)started. Any previously compiled program is
 * deleted, so callers must stop using old program IDs after calling this.
 *
 * Must be called on the thread that owns the current GL context.
 *
 * @throws ShaderCompilerException if compilation or linking fails; the exception carries the driver's info log.
 */
fun compileProgram(fragmentShaderSource: String): Int
```

### Overrides

An override inherits the documentation of the member it overrides (the IDE shows the parent's KDoc).

- An override **must** have its own docblock if its implementation is complicated or does something unexpected,
  such as side effects, caching, unsupported cases or unusual control flow. As with private methods, if a
  reviewer asks for one, add it.
- That docblock covers only what is specific to this implementation. **Don't restate what the parent's
  docblock already says.**
- Our own abstract and interface members (e.g. `GlslReference.doResolve`, `GlslNamedType.getDimension`) are
  documented where they're declared, following the public-method rules above.

### Private methods

Private methods that aren't trivial **must** have a KDoc block of the same kind. What counts as "trivial" is
subjective, so the reviewer decides: **if a reviewer asks for a docblock, add one.**

### Empty docblocks

An empty `/** */` block does not count as documentation. Don't add placeholder blocks. When you touch a function
that has one, either fill it in or, if the function doesn't need one (a trivial private function, or a
straightforward override), delete it.

### Classes and comments

- Non-trivial classes **should** have a class-level KDoc explaining their role and how they fit into the plugin.
  Test classes should say what they cover (see `GlslBuiltinUtilsCacheTest`).
- Inline comments explain *why*, not *what*. Link longer explanations from code to `docs/`
  (e.g. `docs/macro-issues.MD`).
- A `TODO` comment must say what is missing. Don't ship `TODO("Not yet implemented")` in reachable code paths.

## Kotlin

- Don't use `!!` on values that can legitimately be null: PSI children (the parser builds trees for half-typed
  code, so children are often missing while the user types), action and data-context values, and file or
  document lookups. Handle the null by returning early, disabling the action, or returning `null`/an empty
  result. An unhandled exception in a plugin shows up as an "IDE internal error" report blamed on the plugin.
- Instead of a null check followed by `!!`, bind the value to a local:
  `val literal = includeDeclaration.stringLiteral ?: return null`.
- If a null value really would be a bug, use `checkNotNull(x) { "why this can't be null" }` (or `requireNotNull`
  for arguments), so the error report explains the assumption instead of showing a bare
  `NullPointerException`.
- Validate state with `check`/`require` and a message rather than throwing a bare `IllegalStateException`.
- Use a primary constructor rather than a secondary constructor that only assigns fields.
- Prefer `val` and immutable collections. Keep mutable state `private`.
- Throw a specific exception type (see `utils/exceptions`) when callers are expected to handle the failure.

## Logging

- Use the IntelliJ logger: `private val LOG = Logger.getInstance(Foo::class.java)`, declared at the top level of
  the file or in the companion object.
- Never use `println` for diagnostics. Use `LOG.debug` for tracing and `LOG.warn`/`LOG.error` for real problems.
- Shader output meant for the user goes to the run console via the process handler, not to the log.

## IntelliJ Platform

- Access PSI and documents only inside a read action, and modify them only inside a write action.
- Deprecated and internal platform APIs **must not** be used. `./gradlew verifyPlugin` **must** pass without
  deprecation or internal-API warnings, but you do not need to address old issues not caused by your own change.
- State that holds PSI or other project-bound objects **must** be scoped to a project and must not outlive it.
  Builtin-element caches are per-project for this reason (see `GlslBuiltinUtilsCacheTest`).
- Register extensions in `plugin.xml`. Don't instantiate them manually.
- OpenGL calls **must** run on the thread with the current GL context (see `GlContextManager`).

## Tests

- Bug fixes and behavior changes **must** come with a test. Mention the test in the commit message if it
  isn't obvious (e.g. `fixed #include autocompletion bug (testIncludeCompletionAcrossDirectories)`).
- Tests **must** check behavior, not restate definitions. A test that only asserts what a declaration says (e.g.
  that an override returns the constant it was written to return) fails only if someone deletes the line, which
  review catches anyway. A fix that only changes such a declaration or configuration value doesn't need a test.
- Use `BasePlatformTestCase` for feature tests and `ParsingTestCase` for parser golden files. Test methods use the
  JUnit 3 `testXxx` naming that these base classes require.
- New test names **must** describe what they test (`testSetUniformNameOverwritesExistingEntry`), never just
  number it (`testReference7`). This also applies to tests driven by a fixture file. Give the fixture file a
  matching descriptive name.
- Put fixture files in `src/test/testData/<area>/`. When you regenerate golden files (`.txt` parse trees), review
  the diff. Don't accept it blindly.

### Assertion messages

Every test assertion **must** set a message. The message both explains what is being tested and makes failures
easier to understand. Phrase it as the expectation:

```kotlin
// Bad
assertEquals(3, usages.size)

// Good
assertEquals("expected the declaration and both usages of `color` to be found", 3, usages.size)
```

- For `fail(...)`, the argument is the message: `fail("expected a ShaderCompilerException")`.
- `UsefulTestCase.assertThrows(Class, String, ...)` treats its `String` as a pattern the exception message must
  match, not as a failure message. If you need a failure message, use `try { ...; fail("...") } catch (e: Foo) {}`
  instead.
- Golden-file checks such as `ParsingTestCase.doTest(...)` have no message argument and are exempt.

## Commits and changelog

- Keep commit subjects short and lowercase, describing the change (`replace deprecated ReadAction.compute`).
  Reference issues as `#123`.
- Add user-visible changes to `CHANGELOG.md` under the current version's `Added`/`Fixed` sections.

## Existing code

Much of the existing code predates these conventions. For example, 368 of the 428 KDoc blocks in `src/main` are
empty, and most existing tests have neither proper names nor assertion messages.

- **Code you add or change must comply.** If you modify a function, it gets a proper docblock. If you add or
  modify a test, its assertions get messages. You are not required to bring the rest of the file up to standard.
- **New names must follow the naming rules, but existing names are exempt.** Don't rename an existing class,
  function or test as part of another change, even if you modify it. A rename touches every call site, so
  renames go in dedicated rename PRs (see below).
- **Clean-up sweeps are welcome, but must be separate PRs that do one thing**, e.g. "add assertion messages to
  the reference tests" or "remove trailing semicolons". Don't mix a sweep into a feature or bug-fix PR, and don't
  combine several kinds of clean-up into one sweep. This keeps the actual change reviewable and the sweep easy to
  verify.
