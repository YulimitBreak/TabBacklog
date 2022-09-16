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
        browser {
            webpackTask {
                outputFileName = "content_script.js"
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
                implementation(project(":base"))
                implementation(kotlin("stdlib-js"))
            }
        }
    }
}
