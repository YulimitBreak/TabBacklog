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
                implementation(buildsrc.Lib.Kotlin.CoroutinesCoreJs)
                implementation(buildsrc.Lib.Kotlin.CoroutinesCore)
                implementation(project(":base"))
                implementation(kotlin("stdlib-js"))
            }
        }
    }
}
