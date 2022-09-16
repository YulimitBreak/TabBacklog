plugins {
    kotlin("js")
}

repositories {
    google()
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("stdlib-js"))
    implementation(buildsrc.Lib.Kotlin.COROUTINES_CORE_JS)
    implementation(buildsrc.Lib.Kotlin.COROUTINES_CORE)
    implementation(project(":base"))
}

kotlin {
    js(LEGACY) {
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
}