import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import di.AppModule
import di.RepositoryModule
import ui.manager.Manager
import ui.renderApp

fun main() {

    renderApp(rootElementId = "root") {
        val appModule = remember { AppModule(RepositoryModule()) }
        CompositionLocalProvider(
            AppModule.Local provides appModule
        ) {
            Manager()
        }
    }
}