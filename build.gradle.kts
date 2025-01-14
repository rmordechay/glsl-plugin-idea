import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.register
import org.jetbrains.changelog.Changelog
import org.jetbrains.grammarkit.tasks.GenerateLexerTask
import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    kotlin("jvm") version "1.9.21"
    id("org.jetbrains.intellij.platform") version "2.2.1"
    id("org.jetbrains.grammarkit") version "2022.3.2.2"
    id("org.jetbrains.changelog") version "2.2.1"
    java
    idea
}

val pluginVersion: String by project
val javaVersion = 17

group = "glsl.plugin"
version = pluginVersion

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        //This needs to be the *oldest* supported version. verifyPlugin can be used to check it against newer versions.
        intellijIdeaCommunity("2023.3.8")
        testFramework(TestFrameworkType.Platform)
    }
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.opentest4j:opentest4j:1.3.0")
}

idea {
    module {
        isDownloadJavadoc = false
        isDownloadSources = true
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(javaVersion)
        @Suppress("UnstableApiUsage")
        vendor = JvmVendorSpec.JETBRAINS
    }
    sourceCompatibility = JavaVersion.toVersion(javaVersion)
    targetCompatibility = JavaVersion.toVersion(javaVersion)
}

kotlin {
    jvmToolchain(javaVersion)
}

intellijPlatform {
    pluginConfiguration {
        version = pluginVersion

        description = providers.fileContents(layout.projectDirectory.file("plugin-info/description.html")).asText

        val changelog = project.changelog // local variable for configuration cache compatibility

        changeNotes = with(changelog) {
            renderItem(
                (getOrNull(pluginVersion) ?: getUnreleased())
                    .withHeader(false)
                    .withEmptySections(false),
                Changelog.OutputType.HTML
            )
        }

        ideaVersion {
            sinceBuild = "233" // Needs to be equal to the compile target intellij version
            untilBuild = provider { null }
        }
    }

    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")
    }
}

tasks {
    runIde {
        maxHeapSize = "6g"
    }
}

//region grammarkit
run {

    val grammarGenRoot = layout.buildDirectory.dir("generated/sources/grammarkit")
    val rootPackagePath = "glsl"
    val grammarSources = layout.projectDirectory.dir("src/main/grammar")
    val parserDir = grammarGenRoot.map {it.dir("glsl/parser")}
    val lexerDir = grammarGenRoot.map {it.dir("glsl/lexer")}
    val lexerHighlightDir = grammarGenRoot.map {it.dir("glsl/lexer_highlight")}
    val grammarGenDirs = listOf(parserDir, lexerDir, lexerHighlightDir)
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

        register<GenerateLexerTask>("generateLexerHighlight") {
            purgeOldFiles = true
            sourceFile = grammarSources.file("GlslHighlightLexer.flex")
            targetOutputDir = lexerHighlightDir.map { it.dir(rootPackagePath) }

        }
        generateParser {
            purgeOldFiles = true
            sourceFile = grammarSources.file("GlslGrammar.bnf")
            targetRootOutputDir = parserDir
            pathToParser = "$rootPackagePath/_GlslParser.java"
            pathToPsiRoot = "$rootPackagePath/psi"
        }
        register<DefaultTask>("generateGrammars") {
            group = "grammarkit"
            dependsOn("generateLexer", "generateLexerHighlight", "generateParser")
        }

        compileJava {
            dependsOn("generateGrammars")
        }

        compileKotlin {
            dependsOn("generateGrammars")
        }
    }
}
//endregion