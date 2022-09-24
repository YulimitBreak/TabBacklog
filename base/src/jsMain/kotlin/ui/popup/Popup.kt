package ui.popup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import di.ModuleLocal
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Main
import org.jetbrains.compose.web.dom.Text
import ui.common.bookmark.BookmarkContent

@Composable
fun Popup() {
    val scope = rememberCoroutineScope()
    val appModule = ModuleLocal.App.current
    val model = remember { appModule.createPopupBaseModel(scope) }

    Main(
        attrs = {
            style {
                height(100.percent)
                width(100.percent)
            }
        }
    ) {
        Button(
            attrs = {
                style {
                    width(50.percent)
                }
                onClick {
                    model.openManager()
                }
            }
        ) {
            Text("Open manager")
        }

        BookmarkContent(model.state.bookmark, attrs = {
            style {
                width(100.percent)
                height(100.percent)
            }
        })
    }
}