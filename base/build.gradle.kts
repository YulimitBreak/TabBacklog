plugins {
    kotlin("multiplatform")
    id("io.github.sergeshustoff.dikt")
    id("org.jetbrains.compose")
}

repositories {
    google()
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
}

kotlin {
    js(IR) {
        binaries.executable()
        useCommonJs()
        browser {
            webpackTask {
                outputFileName = "base.js"
                sourceMaps = false
                report = true
            }
            distribution {
                directory = File("$projectDir/../build/distributions/")
            }
        }
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation(buildsrc.Lib.Kotlin.COROUTINES_CORE_JS)
                implementation(buildsrc.Lib.Kotlin.COROUTINES_CORE)
                implementation(buildsrc.Lib.TIME)
                implementation(compose.web.core)
                implementation(compose.runtime)
            }
        }
    }
}