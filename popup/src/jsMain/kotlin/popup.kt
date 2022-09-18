import androidx.compose.runtime.remember
import di.AppModule
import org.jetbrains.compose.web.renderComposable
import ui.popup.Popup

fun main() {

    renderComposable(rootElementId = "root") {
        Popup(
            remember { AppModule() }
        )
    }
}