package ui.popup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributesBuilder
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.silk.components.forms.Button
import di.ModuleLocal
import entity.Bookmark
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Main
import org.jetbrains.compose.web.dom.Text
import ui.common.DefaultLocalProvider
import ui.common.basecomponent.LoadableView
import ui.common.bookmark.editor.BookmarkEditor
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

        Main(attrs = Modifier.fillMaxWidth().asAttributesBuilder()) {
            Button(
                onClick = { model.openManager() },
                Modifier.width(50.percent),
            ) {
                Text("Open manager")
            }

            LoadableView(
                model.state.bookmark,
                Modifier.fillMaxWidth().minHeight(300.px)
            ) { bookmark: Bookmark, modifier: Modifier ->
                BookmarkEditor(bookmark, modifier.asAttributesBuilder())
            }
        }
    }

}