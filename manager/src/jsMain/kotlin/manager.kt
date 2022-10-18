import ui.manager.Manager
import ui.renderApp

fun main() {

    renderApp(rootElementId = "root") {
        Manager()
    }
}