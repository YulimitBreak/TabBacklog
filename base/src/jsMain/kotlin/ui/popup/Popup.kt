package ui.popup

import androidx.compose.runtime.Composable
import app.softwork.routingcompose.HashRouter
import app.softwork.routingcompose.Router
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributesBuilder
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.silk.components.icons.fa.FaListCheck
import data.BrowserInteractor
import entity.SingleBookmarkSource
import org.jetbrains.compose.web.dom.Main
import org.jetbrains.compose.web.dom.Text
import ui.common.DefaultLocalProvider
import ui.common.basecomponent.RowButton
import ui.page.editor.BookmarkEditor
import ui.page.summary.BookmarkSummary

@Composable
fun Popup() {
    // val scope = rememberCoroutineScope()
    // val appModule = AppModule.Local.current
    // val model: PopupBaseModel = remember { appModule.createPopupBaseModel(scope) }

    DefaultLocalProvider {
        Main(attrs = Modifier.fillMaxWidth().asAttributesBuilder()) {
            HashRouter("/") {
                val router = Router.current
                route("/") {
                    BookmarkSummary(
                        target = SingleBookmarkSource.CurrentTab,
                        onEditRequest = { router.navigate("/edit") },
                        firstButton = {
                            val browserInteractor = BrowserInteractor.Local.current
                            RowButton(onClick = { browserInteractor.openManager() }) {
                                FaListCheck()
                                Text("Open manager")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                route("/edit") {
                    BookmarkEditor(
                        target = SingleBookmarkSource.CurrentTab,
                        onNavigateBack = { router.navigate("/") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

}