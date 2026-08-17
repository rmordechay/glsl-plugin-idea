### [Donation with PayPal to the maintainer](https://www.paypal.me/Zonkodonko)

# GLSL Plugin
GLSL plugin support for JetBrains IDE's.

This plugin is based on the [GLSL plugin](https://plugins.jetbrains.com/plugin/18470-glsl) by [walt-grace](https://github.com/rmordechay), which is no longer maintained.
It adds new features and bugfixes.

Feel free to report any issue, problem, bug or add any request.



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

### [Donation with PayPal to OG founder of this plugin](https://www.paypal.com/donate/?hosted_button_id=FVDM2Z3ESPC5Y)

