[![Downloads](https://img.shields.io/jetbrains/plugin/d/18470-glsl)](https://plugins.jetbrains.com/plugin/18470-glsl/reviews)
[![Rating](https://img.shields.io/jetbrains/plugin/r/stars/18470-glsl)](https://plugins.jetbrains.com/plugin/18470-glsl/reviews)

### [Donation with PayPal to the founder of this plugin](https://www.paypal.com/donate/?hosted_button_id=FVDM2Z3ESPC5Y)
### [Donation with PayPal to the guy who made the shader preview](https://www.paypal.me/Zonkodonko)

# GLSL Plugin
GLSL plugin support for JetBrains IDE's.
Feel free to report any issue, problem, bug or add any request.

[Plugin Page](https://plugins.jetbrains.com/plugin/18470-glsl)


## Build & Run
``` shell
git clone https://github.com/rmordechay/glsl-plugin-idea.git
```
Assuming you're developing with Intellij (and you want to develop with Intellij):
1. **Generate grammar**. Execute the `generateGrammarClean` task from _build.gradle.kts_ or under _Tasks/other_ if you use the Gradle tab.
   Gradle builds do this automatically, but the IDE can only resolve the generated parser classes once they exist, so run it once after cloning.
2. **Run**. Execute the `runIde` task (Intellij will build the project and then run the instance).

\* If you're only interested in building the project without running it you can use task `buildPlugin`.

## Test
Just execute the `test` task from the Gradle tab or run specific classes or tests from within the ide.

## Releasing
See [RELEASING.md](RELEASING.md) for how releases and alpha builds are published, and how to publish from your own machine.

