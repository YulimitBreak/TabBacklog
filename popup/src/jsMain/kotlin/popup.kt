import androidx.compose.runtime.*
import browser.tabs.CreateCreateProperties
import browser.tabs.QueryQueryInfo
import browser.tabs.Tab
import di.ExternalModuleImplementation
import di.ExternalModuleImplementation2
import di.TestModule
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable

fun main() {

    renderComposable(rootElementId = "root") {

        val module1 = remember {
            TestModule(ExternalModuleImplementation())
        }

        val module2 = remember {
            TestModule(ExternalModuleImplementation2())
        }

        Text("Module1 Target: " + module1.provideTarget().source.number.toString())
        Br()
        Text("Module1 Source: " + module1.provideSource().number.toString())
        Br()
        Text("Module2 Target: " + module2.provideTarget().source.number.toString())
        Br()
        Text("Module2 Source: " + module2.provideSource().number.toString())
        Br()

        var count: Int by remember { mutableStateOf(0) }
        var tabs by remember { mutableStateOf(emptyList<Tab>()) }
        var error by remember { mutableStateOf("") }

        Button(
            attrs = {
                onClick {
                    browser.tabs.create(CreateCreateProperties {
                        url = "manager.html"
                    })
                }
            }
        ) {
            Text("Open manager")
        }

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