plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

repositories {
    google()
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    js(IR) {
        binaries.executable()
        useCommonJs()
        browser {
            webpackTask {
                outputFileName = "popup.js"
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
                implementation(compose.web.core)
                implementation(compose.runtime)
            }
        }
    }
}