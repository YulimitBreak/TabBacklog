import buildsrc.Lib

plugins {
    kotlin("multiplatform")
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
                outputFileName = "background.js"
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
                implementation(Lib.Kotlin.CoroutinesCoreJs)
                implementation(Lib.Kotlin.CoroutinesCore)
                implementation(project(":base"))
                implementation(kotlin("stdlib-js"))
            }
        }
    }
}