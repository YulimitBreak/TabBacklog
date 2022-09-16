import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension

plugins {
    kotlin("multiplatform") version "1.7.10"
    id("org.jetbrains.compose") version "1.2.0-beta01"
}

group = "com.gmail.ivkhegay.tablibrary"
version = "0.1.0"

allprojects {
    repositories {
        google()
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}

repositories {
    google()
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
}

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))

                runtimeOnly(npm("webextension-polyfill", "0.10.0"))
                implementation(devNpm("webpack-bundle-analyzer", "4.6.1"))

                compileOnly(project(":background"))
                compileOnly(project(":content"))
                compileOnly(project(":popup"))
            }
        }
    }
}

rootProject.plugins.withType<YarnPlugin> {
    rootProject.the<YarnRootExtension>().download = true
}

tasks {
    val extensionFolder = "build/extension"
    val copyBundleFile = register<Copy>("copyBundleFile") {
        subprojects.forEach { prj ->
            try {
                dependsOn(prj.tasks.getByName("jsBrowserDistribution"))
            } catch (_: UnknownTaskException) {
                // skip
            }
        }
        from("build/distributions") {
            include("*.js")
        }
        into(extensionFolder)
    }
    val copyResources = register<Copy>("copyResources") {
        val resourceFolder = "src/main/resources"
        from(
            "$resourceFolder/manifest.json",
            "$resourceFolder/icons",
            "$resourceFolder/html",
            "$resourceFolder/css"
        )
        into(extensionFolder)
    }
    val copyPolyfill = register<Copy>("copyPolyfill") {
        from("build/js/node_modules/webextension-polyfill/dist") {
            include("browser-polyfill.min.js")
            include("browser-polyfill.min.js.map")
        }
        into(extensionFolder)
    }
    val extension = register<Zip>("extension") {
        dependsOn(copyResources, copyPolyfill, copyBundleFile)
        from(extensionFolder)
        into("build")
    }
}