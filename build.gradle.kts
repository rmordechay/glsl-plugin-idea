import org.jetbrains.changelog.Changelog
import org.jetbrains.grammarkit.tasks.GenerateLexerTask

import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
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

val pluginVersion: String by project
val platformVersion: String by project
val sinceVersion: String by project

group = "glsl.plugin"
version = pluginVersion

kotlin {
    jvmToolchain(21)
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

    runtimeOnly("org.lwjgl:lwjgl::natives-linux")
    runtimeOnly("org.lwjgl:lwjgl-opengl::natives-linux")

    runtimeOnly("org.lwjgl:lwjgl::natives-macos")
    runtimeOnly("org.lwjgl:lwjgl-opengl::natives-macos")

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
    }
}

intellijPlatform {
    pluginConfiguration {
        version = pluginVersion
        description = file("plugin-info/description.html").readText()
        changeNotes = changelog.renderItem(changelog.get(pluginVersion), Changelog.OutputType.HTML)
        ideaVersion {
            sinceBuild = "223"
        }
    }
    publishing {
        token = System.getenv("PUBLISH_TOKEN")
    }
    pluginVerification {
        ides {
            select {
                types = listOf(IntelliJPlatformType.IntellijIdeaCommunity)
            }
        }
    }
}

tasks {
    val buildSearchableOptionsEnabled =
        providers.gradleProperty("buildSearchableOptionsEnabled").map(String::toBoolean).orElse(false)
    compileJava {
        sourceCompatibility = JavaVersion.VERSION_21.majorVersion
        targetCompatibility = JavaVersion.VERSION_21.majorVersion
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

        runIde { //diables kubenetes because its trash and dumps our logs with bullshit
            maxHeapSize = "6g"

            doFirst {
                val disabledIds = listOf(
                    "com.intellij.kubernetes",
                )

                val sandboxRoot = layout.buildDirectory.dir("idea-sandbox").get().asFile

                val candidateConfigDirs = sandboxRoot
                    .listFiles()
                    ?.filter { it.isDirectory }
                    ?.map { it.resolve("config") }
                    ?.filter { it.isDirectory }
                    .orEmpty()

                val configDir = when {
                    candidateConfigDirs.size == 1 -> candidateConfigDirs.single()
                    candidateConfigDirs.isNotEmpty() -> candidateConfigDirs.maxBy { it.lastModified() } // nimm die "aktuellste"
                    else -> sandboxRoot.resolve("config") // Fallback für ältere Layouts
                }

                configDir.mkdirs()
                configDir.resolve("disabled_plugins.txt")
                    .writeText(disabledIds.joinToString(System.lineSeparator()))
            }
        }
    }
}
//endregion