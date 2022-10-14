package ui.page.summary

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.silk.components.icons.fa.FaBookOpen
import com.varabyte.kobweb.silk.components.icons.fa.FaStar
import com.varabyte.kobweb.silk.components.icons.fa.FaTrash
import com.varabyte.kobweb.silk.components.icons.fa.IconStyle
import com.varabyte.kobweb.silk.components.text.SpanText
import di.ModuleLocal
import entity.Bookmark
import org.jetbrains.compose.web.css.px
import ui.common.basecomponent.RowButton
import ui.common.bookmark.BookmarkTitleView


@Composable
fun BookmarkSummary(
    bookmark: Bookmark,
    onBookmarkUpdate: (Bookmark) -> Unit,
    modifier: Modifier = Modifier
) {
    val appModule = ModuleLocal.App.current
    val scope = rememberCoroutineScope()

    val model: BookmarkSummaryModel = remember { appModule.createBookmarkSummaryModel(scope) }

    Column(modifier) {
        Row(Modifier.fillMaxWidth()) {

            RowButton(onClick = { model.openManager() }) {
                FaBookOpen()
                SpanText("Open manager")
            }

            Spacer()

            RowButton(
                onClick = { onBookmarkUpdate(bookmark.copy(favorite = !bookmark.favorite)) },
                Modifier.margin(right = 8.px)
            ) {
                FaStar(style = if (bookmark.favorite) IconStyle.FILLED else IconStyle.OUTLINE)
                SpanText("Favorite")
            }
            RowButton(onClick = { onBookmarkUpdate(bookmark.copy(creationDate = null)) }) {
                FaTrash()
                SpanText("Delete")
            }
        }
        BookmarkTitleView(bookmark.title, bookmark.favicon, bookmark.url, Modifier.margin(top = 8.px))
    }
}