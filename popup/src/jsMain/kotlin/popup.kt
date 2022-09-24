import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import di.AppModule
import di.RepositoryModule
import org.jetbrains.compose.web.renderComposable
import ui.popup.Popup

fun main() {

    renderComposable(rootElementId = "root") {
        val appModule = remember { AppModule(RepositoryModule()) }
        CompositionLocalProvider() {
            Popup(

            )
        }
    }
}