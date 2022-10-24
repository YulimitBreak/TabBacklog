package ui.popup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import app.softwork.routingcompose.HashRouter
import app.softwork.routingcompose.Router
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributesBuilder
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import di.ModuleLocal
import entity.SingleBookmarkTarget
import org.jetbrains.compose.web.dom.Main
import ui.common.DefaultLocalProvider
import ui.page.editor.BookmarkEditor
import ui.page.summary.BookmarkSummary

@Composable
fun Popup() {
    val scope = rememberCoroutineScope()
    val appModule = ModuleLocal.App.current
    // val model: PopupBaseModel = remember { appModule.createPopupBaseModel(scope) }

    DefaultLocalProvider {
        Main(attrs = Modifier.fillMaxWidth().asAttributesBuilder()) {
            HashRouter("/") {
                val router = Router.current
                route("/") {
                    BookmarkSummary(
                        target = SingleBookmarkTarget.CurrentTab,
                        onEditRequest = { router.navigate("/edit") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                route("/edit") {
                    BookmarkEditor(
                        target = SingleBookmarkTarget.CurrentTab,
                        onNavigateBack = { router.navigate("/") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

}