[![Downloads](https://img.shields.io/jetbrains/plugin/d/18470-glsl)](https://plugins.jetbrains.com/plugin/18470-glsl/reviews)
[![Rating](https://img.shields.io/jetbrains/plugin/r/stars/18470-glsl)](https://plugins.jetbrains.com/plugin/18470-glsl/reviews)

# GLSL Plugin
GLSL plugin support for JetBrains IDE's.
Feel free to report any issue, problem, bug or add any request.

[Plugin Page](https://plugins.jetbrains.com/plugin/18470-glsl)


## Build & Run
``` shell
git clone https://github.com/walt-grace/glsl-plugin-idea.git
```
Assuming you're developing with Intellij (and you want to develop with Intellij):
1. **Generate grammar**. Execute the `generateGrammarClean` task from _gradle.build_ file or under _Tasks/other_ if you use the Gradle tab. 
2. **Run**. Execute the `runIde` task (Intellij will build the project and then run the instance).

\* If you're only interested in building the project without running it you can use task `buildPlugin` after step 1.

## Test
Just execute the `test` task from the Gradle tab or run specific classes or tests from within the ide.

### [Donation](https://www.paypal.com/donate/?hosted_button_id=FVDM2Z3ESPC5Y)