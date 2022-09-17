plugins {
    kotlin("multiplatform")
    id("io.github.sergeshustoff.dikt")
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
                implementation(buildsrc.Lib.Kotlin.COROUTINES_CORE_JS)
                implementation(buildsrc.Lib.Kotlin.COROUTINES_CORE)
                implementation(kotlin("stdlib-js"))
            }
        }
    }
}