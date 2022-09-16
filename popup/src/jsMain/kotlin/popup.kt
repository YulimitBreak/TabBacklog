import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import browser.action.SetBadgeTextDetails
import browser.tabs.QueryQueryInfo
import browser.tabs.Tab
import common.collect
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable

fun main() {
    var count: Int by mutableStateOf(0)
    var tabs by mutableStateOf(emptyList<Tab>())

    renderComposable(rootElementId = "root") {
        rememberCoroutineScope().launch {
            tabs = browser.tabs.getAllInWindow().await().toList()

        }
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
    }
}