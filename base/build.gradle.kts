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
    maven(url = buildsrc.Lib.Kobweb.Repo)
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
                implementation(buildsrc.Lib.Kotlin.CoroutinesCoreJs)
                implementation(buildsrc.Lib.Kotlin.CoroutinesCore)
                implementation(buildsrc.Lib.Time)
                implementation(buildsrc.Lib.IndexedDb)
                implementation(buildsrc.Lib.Kobweb.Compose)
                implementation(buildsrc.Lib.Kobweb.ComposeExt)
                implementation(buildsrc.Lib.Kobweb.Silk)
                implementation(buildsrc.Lib.Kobweb.SilkFaIcons)
                implementation(buildsrc.Lib.Routing)
                implementation(compose.web.core)
                implementation(compose.runtime)
            }
        }
    }
}