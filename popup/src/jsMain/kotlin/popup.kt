import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import di.AppModule
import di.ModuleLocal
import di.RepositoryModule
import ui.popup.Popup
import ui.renderApp

fun main() {

    renderApp(rootElementId = "root") {
        val appModule = remember { AppModule(RepositoryModule()) }
        CompositionLocalProvider(
            ModuleLocal.App provides appModule
        ) {
            Popup(

            )
        }
    }
}