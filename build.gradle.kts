import org.jetbrains.changelog.Changelog
import org.jetbrains.intellij.platform.gradle.tasks.GenerateLexerTask

import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("java")
    id("idea")
    kotlin("plugin.serialization") version "2.2.0"
    alias(libs.plugins.kotlin) // Kotlin support
    alias(libs.plugins.intelliJPlatform) // IntelliJ Platform Gradle Plugin
    alias(libs.plugins.changelog) // Gradle Changelog Plugin
    alias(libs.plugins.grammarKit)
}

val pluginVersion: String = providers.gradleProperty("pluginVersion").get()
val platformVersion: String = providers.gradleProperty("platformVersion").get()
val sinceVersion: String = providers.gradleProperty("sinceVersion").get()

group = "glsl.plugin"
version = pluginVersion

kotlin {
    jvmToolchain(25)
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
        snapshots()
    }
}


dependencies {
    intellijPlatform {
        intellijIdea(platformVersion) { useInstaller = false }
        testFramework(TestFrameworkType.Platform)
    }
    testImplementation(libs.junit)
    testImplementation(libs.opentest4j)

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

    implementation(platform(libs.lwjgl.bom))
    implementation(libs.lwjgl)
    implementation(libs.lwjgl.opengl)
    implementation(libs.lwjgl.jawt)

    runtimeOnly("org.lwjgl:lwjgl::natives-windows")
    runtimeOnly("org.lwjgl:lwjgl-opengl::natives-windows")

    runtimeOnly("org.lwjgl:lwjgl::natives-windows-arm64")
    runtimeOnly("org.lwjgl:lwjgl-opengl::natives-windows-arm64")

    runtimeOnly("org.lwjgl:lwjgl::natives-linux")
    runtimeOnly("org.lwjgl:lwjgl-opengl::natives-linux")

    runtimeOnly("org.lwjgl:lwjgl::natives-linux-arm64")
    runtimeOnly("org.lwjgl:lwjgl-opengl::natives-linux-arm64")

    runtimeOnly("org.lwjgl:lwjgl::natives-macos")
    runtimeOnly("org.lwjgl:lwjgl-opengl::natives-macos")

    runtimeOnly("org.lwjgl:lwjgl::natives-macos-arm64")
    runtimeOnly("org.lwjgl:lwjgl-opengl::natives-macos-arm64")

    implementation(libs.lwjgl3.awt) {
        isTransitive = false
    }
}

configurations.configureEach {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.lwjgl") {
            useVersion(libs.versions.lwjgl.get())
            because("All LWJGL artifacts must be compatible with lwjgl3-awt")
        }
        if (requested.group == "org.jetbrains.kotlin" && requested.name == "kotlin-stdlib") {
            useVersion(libs.versions.kotlin.get())
            because("kotlin-stdlib must match the Kotlin compiler/IDE platform version, not the older version pulled in transitively by kotlinx-serialization-json")
        }
    }
}

intellijPlatform {
    pluginConfiguration {
        version = pluginVersion
        description = file("plugin-info/description.html").readText()
        changeNotes = changelog.renderItem(changelog.get(pluginVersion), Changelog.OutputType.HTML)
        ideaVersion {
            sinceBuild = sinceVersion
        }
    }
    publishing {
        token = System.getenv("PUBLISH_TOKEN")
    }
    pluginVerification {
        ides {
            recommended()
        }
    }
}

tasks {
    val buildSearchableOptionsEnabled =
        providers.gradleProperty("buildSearchableOptionsEnabled").map(String::toBoolean).orElse(false)
    compileJava {
        sourceCompatibility = JavaVersion.VERSION_25.majorVersion
        targetCompatibility = JavaVersion.VERSION_25.majorVersion
    }

    runIde {
        maxHeapSize = "6g"
    }
}

//region grammars
run {
    val grammarGenRoot = layout.buildDirectory.dir("generated/sources/grammarkit")
    val rootPackagePath = "glsl"
    val grammarSources = layout.projectDirectory.dir("grammar")

    val parserDir = grammarGenRoot.map { it.dir("glsl/parser") }
    val lexerDir = grammarGenRoot.map { it.dir("glsl/lexer") }
    val highlightLexerDir = grammarGenRoot.map { it.dir("glsl/highlight") }

    val grammarGenDirs = listOf(parserDir, lexerDir, highlightLexerDir)

    sourceSets {
        main {
            java {
                grammarGenDirs.forEach { srcDir(it) }
            }
        }
    }

    idea {
        module {
            grammarGenDirs.forEach {
                val file = it.get().asFile
                sourceDirs.add(file)
                generatedSourceDirs.add(file)
            }
            sourceDirs.add(grammarSources.asFile)
        }
    }

    tasks {
        generateLexer {
            purgeOldFiles = true
            sourceFile = grammarSources.file("GlslLexer.flex")
            targetOutputDir = lexerDir.map { it.dir(rootPackagePath) }
        }
        val generateHighlightLexer = register<GenerateLexerTask>("generateHighlightLexer") {
            purgeOldFiles = true
            sourceFile = grammarSources.file("GlslHighlightLexer.flex")
            targetOutputDir = highlightLexerDir.map { it.dir(rootPackagePath) }
        }
        generateParser {
            purgeOldFiles = true
            sourceFile = grammarSources.file("GlslGrammar.bnf")
            targetRootOutputDir = parserDir
            pathToParser = "$rootPackagePath/_GlslParser.java"
            pathToPsiRoot = "$rootPackagePath/psi"
        }
        register("generateGrammarClean") {
            dependsOn(generateLexer, generateParser, generateHighlightLexer)
        }
        compileJava {
            dependsOn("generateGrammarClean")
        }
        compileKotlin {
            dependsOn("generateGrammarClean")
        }

        runIde {
            maxHeapSize = "6g"
        }

        prepareSandbox {
            // Kubernetes is trash and dumps our logs with bullshit.
            disabledPlugins.add("com.intellij.kubernetes")
        }

        prepareTestSandbox {
            // Vue's LSP service crashes on init in the headless test sandbox and spams
            // unrelated tests with noise; the plugin doesn't need it.
            disabledPlugins.add("org.jetbrains.plugins.vue")
        }
    }
}
//endregion