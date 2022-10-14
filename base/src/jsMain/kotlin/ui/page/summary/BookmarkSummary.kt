package ui.page.summary

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.icons.fa.FaBookOpen
import com.varabyte.kobweb.silk.components.icons.fa.FaStar
import com.varabyte.kobweb.silk.components.icons.fa.FaTrash
import com.varabyte.kobweb.silk.components.icons.fa.IconStyle
import com.varabyte.kobweb.silk.components.text.SpanText
import di.ModuleLocal
import entity.Bookmark
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.px
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

            Button(
                onClick = {
                    model.openManager()
                },
                Modifier.padding(4.px)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FaBookOpen(
                        modifier = Modifier.fontSize(1.em).margin(right = 4.px),
                    )
                    SpanText("Open manager")
                }
            }

            Spacer()

            Button(
                onClick = { onBookmarkUpdate(bookmark.copy(favorite = !bookmark.favorite)) },
                Modifier.padding(4.px).margin(right = 8.px)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FaStar(
                        modifier = Modifier.fontSize(1.em).margin(right = 4.px),
                        style = if (bookmark.favorite) IconStyle.FILLED else IconStyle.OUTLINE
                    )
                    SpanText("Favorite")
                }
            }
            Button(
                onClick = { onBookmarkUpdate(bookmark.copy(creationDate = null)) },
                Modifier.padding(4.px)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FaTrash(
                        modifier = Modifier.fontSize(1.em).margin(right = 4.px),
                    )
                    SpanText("Delete")
                }
            }
        }
        BookmarkTitleView(bookmark.title, bookmark.favicon, bookmark.url, Modifier.margin(top = 8.px))
    }
}