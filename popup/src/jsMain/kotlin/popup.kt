import androidx.compose.runtime.*
import browser.tabs.QueryQueryInfo
import browser.tabs.Tab
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable

fun main() {

    renderComposable(rootElementId = "root") {

        var count: Int by remember { mutableStateOf(0) }
        var tabs by remember { mutableStateOf(emptyList<Tab>()) }
        var error by remember { mutableStateOf("") }

        rememberCoroutineScope().launch {
            try {
                tabs = browser.tabs.query(QueryQueryInfo {
                    windowId = browser.windows.WINDOW_ID_CURRENT.toInt()
                }).await().toList()
            } catch (e: Throwable) {
                error = (e.message ?: "") + e.cause?.message
            }
        }
        Div({ style { width(400.px) } })
        Div({ style { padding(25.px) } }) {
            Button(attrs = {
                onClick { count -= 1 }
            }) {
                Text("-")
            }

            Span({ style { padding(15.px) } }) {
                Text("$count")
            }

            Button(attrs = {
                onClick { count += 1 }
            }) {
                Text("+")
            }
        }
        Div({
            style { padding(30.px) }
        }) {
            Text(tabs.size.toString())
        }
        Text(error)
    }
}