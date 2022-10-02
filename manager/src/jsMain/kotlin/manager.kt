import org.jetbrains.compose.web.renderComposable
import ui.manager.Manager

fun main() {

    renderComposable(rootElementId = "root") {
        Manager()
    }
}