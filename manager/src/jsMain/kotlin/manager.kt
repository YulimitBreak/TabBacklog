import ui.common.renderApp
import ui.manager.Manager

fun main() {

    renderApp(rootElementId = "root") {
        Manager()
    }
}