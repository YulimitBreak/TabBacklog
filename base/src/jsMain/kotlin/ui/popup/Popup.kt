package ui.popup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import app.softwork.routingcompose.HashRouter
import app.softwork.routingcompose.Router
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributesBuilder
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import di.ModuleLocal
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Main
import ui.common.DefaultLocalProvider
import ui.common.basecomponent.LoadableView
import ui.common.styles.MainStyle
import ui.common.styles.TooltipStyle
import ui.common.styles.UtilStyle
import ui.page.editor.BookmarkEditor
import ui.page.summary.BookmarkSummary

@Composable
fun Popup() {
    val scope = rememberCoroutineScope()
    val appModule = ModuleLocal.App.current
    val model: PopupBaseModel = remember { appModule.createPopupBaseModel(scope) }

    Style(UtilStyle)
    Style(MainStyle)
    Style(TooltipStyle)

    DefaultLocalProvider {
        LoadableView(
            model.state.bookmark,
            Modifier.minHeight(100.px).fillMaxWidth()
        ) { bookmark, modifier ->
            Main(attrs = modifier.asAttributesBuilder()) {
                HashRouter("/") {
                    val router = Router.current
                    route("/") {
                        BookmarkSummary(
                            bookmark,
                            onBookmarkUpdate = { model.replaceBookmark(it) },
                            onEditRequest = { router.navigate("/edit") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    route("/edit") {
                        BookmarkEditor(bookmark, Modifier.fillMaxWidth().asAttributesBuilder())
                    }
                }
            }
        }
    }

}