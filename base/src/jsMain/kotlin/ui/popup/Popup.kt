package ui.popup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import di.ModuleLocal
import org.jetbrains.compose.web.css.Style
import ui.common.DefaultLocalProvider
import ui.common.styles.MainStyle
import ui.common.styles.TooltipStyle
import ui.common.styles.UtilStyle

@Composable
fun Popup() {
    val scope = rememberCoroutineScope()
    val appModule = ModuleLocal.App.current
    val model = remember { appModule.createPopupBaseModel(scope) }

    Style(UtilStyle)
    Style(MainStyle)
    Style(TooltipStyle)

    DefaultLocalProvider {

    }

    /*
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
    }*/
}