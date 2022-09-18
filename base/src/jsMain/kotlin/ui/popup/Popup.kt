package ui.popup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import di.AppModule
import di.PopupModule
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import ui.common.TextButton

@Composable
fun Popup(appModule: AppModule) {
    val scope = rememberCoroutineScope()
    val module = remember { PopupModule(appModule, scope) }
    val model = module.model

    TextButton("Open manager") {
        model.openManager()
    }

    P(attrs = {
        style {
            width(300.px)
        }
    }) {
        Text(model.uiState.bookmark.toString())
    }
}