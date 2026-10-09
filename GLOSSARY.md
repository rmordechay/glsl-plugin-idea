# Glossary

The abbreviations and acronyms that may be used in names in this project. Any abbreviation not listed here should be
spelled out. See [CODING-CONVENTIONS.md](CODING-CONVENTIONS.md#acronyms-and-abbreviations) for the rules, including
which terms may be added here.

## Grammar terms

These come from the grammar (`grammar/GlslGrammar.bnf`), so they also appear in the generated PSI classes
(`GlslPpIncludeDeclaration`, `GlslExprNoAssignment`, …). They are legacy: hand-written code may use them where it
refers to the corresponding grammar or PSI concept, but no new grammar terms are added.

| Term     | Meaning                                                                                                      |
|----------|--------------------------------------------------------------------------------------------------------------|
| `Pp`     | Preprocessor: the `#` directives (`#define`, `#include`, `#pragma`, `#version`, `#ifdef`, …)                 |
| `Expr`   | Expression                                                                                                   |
| `Func`   | Function. Only in references to grammar rules such as `func_header_with_params`; otherwise write `function`. |
| `Init`   | Initializer, as in `init_declarator_variable`                                                                |
| `Params` | Parameters                                                                                                   |
| `Id`     | Identifier, as in `layout_qualifier_id`                                                                      |
| `Spirv`  | SPIR-V, the Khronos intermediate shader representation; GLSL has `spirv_*` extension qualifiers              |

## Domain acronyms

| Term   | Meaning                                                                                |
|--------|----------------------------------------------------------------------------------------|
| `Glsl` | OpenGL Shading Language. Prefix for the plugin's classes.                              |
| `Gl`   | OpenGL                                                                                 |
| `Psi`  | Program Structure Interface: the IntelliJ Platform's syntax-tree API                   |
| `Ast`  | Abstract syntax tree: the lower-level node tree the PSI is built on                    |
| `MC`   | Minecraft (shader packs, OptiFine/Iris/Sodium includes). Always written as `MC`, not `Mc` |
