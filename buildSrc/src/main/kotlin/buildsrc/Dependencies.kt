
package buildsrc

object Lib {
    object Kotlin {
        const val CoroutinesCoreJs = "org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.6.4"
        const val CoroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4"
    }

    const val Time = "org.jetbrains.kotlinx:kotlinx-datetime:0.4.0"
    // const val IndexedDb = "com.juul.indexeddb:core:0.2.3"
    const val IndexedDb = "com.github.ShaggyDemiurge.indexeddb:core:3ac5dc7658"

    object Kobweb {
        private const val Version = "0.10.0"
        const val Repo = "https://us-central1-maven.pkg.dev/varabyte-repos/public"
        const val Compose = "com.varabyte.kobweb:kobweb-compose:$Version"
        const val ComposeExt = "com.varabyte.kobweb:web-compose-ext:$Version"
        const val Silk = "com.varabyte.kobweb:kobweb-silk-widgets:$Version"
        const val SilkFaIcons = "com.varabyte.kobweb:kobweb-silk-icons-fa:$Version"
    }

    const val Routing = "app.softwork:routing-compose:0.2.9"

    object Kotest {
        private const val Version = "5.5.1"
        const val Core = "io.kotest:kotest-framework-engine:$Version"
        const val Assertions = "io.kotest:kotest-assertions-core:$Version"
        const val PropertyTesting = "io.kotest:kotest-property:$Version"
    }
}