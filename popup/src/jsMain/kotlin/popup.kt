import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import di.AppModule
import di.ModuleLocal
import di.RepositoryModule
import ui.common.renderApp
import ui.popup.Popup

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