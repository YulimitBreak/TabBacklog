package ui.popup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import di.AppModule
import di.PopupModule
import ui.common.TextButton

@Composable
fun Popup(appModule: AppModule) {
    val scope = rememberCoroutineScope()
    val module = remember { PopupModule(appModule, scope) }
    val model = remember { module.provideModel() }

    TextButton("Open manager") {
        model.openManager()
    }
}