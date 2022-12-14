package ui.page.bookmarklist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.css.UserSelect
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.graphics.toCssColor
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.title
import com.varabyte.kobweb.compose.ui.modifiers.userSelect
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.silk.components.text.SpanText
import di.AppModule
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.minus
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Text
import ui.common.basecomponent.LoadingTable
import ui.common.basecomponent.ModalDialog
import ui.common.basecomponent.RowButton
import ui.common.basecomponent.Toggle
import ui.common.bookmark.BookmarkTableView
import ui.page.export.ExportDialogView
import ui.styles.Palette
import ui.styles.backgroundColors
import ui.styles.primaryColors

@Composable
fun BookmarkList(modifier: Modifier = Modifier, onBookmarkSelect: (urls: Set<String>) -> Unit) {

    val appModule = AppModule.Local.current
    val scope = rememberCoroutineScope()
    val onBookmarkSelectState = rememberUpdatedState(BookmarkListModel.OnBookmarkSelect(onBookmarkSelect))
    val model: BookmarkListModel = remember() {
        appModule.createBookmarkListModel(scope, onBookmarkSelectState)
    }

    var exportDialogVisible by remember { mutableStateOf(false) }

    ModalDialog(exportDialogVisible, onDismiss = { exportDialogVisible = false }) {
        ExportDialogView(
            modifier = Modifier.backgroundColors().padding(32.px),
            onDismiss = { exportDialogVisible = false })
    }

    Column(modifier) {
        BookmarkSearchView(
            model.searchConfig,
            onConfigChange = {
                model.onSearchConfigChange(it)

            },
            modifier = Modifier.width(100.percent - 32.px).padding(leftRight = 16.px, topBottom = 8.px)
        )

        LoadingTable(
            model.bookmarkListState.list,
            model.bookmarkListState.isLoading,
            Modifier.margin(leftRight = 16.px).width(100.percent - 32.px)
                .minHeight(20.percent)
                .flexGrow(1)
                .border(1.px, LineStyle.Solid, Palette.Local.current.onBackground.toCssColor()),
            onLoadMore = {
                model.requestMoreBookmarks()
            }.takeIf { !model.bookmarkListState.reachedEnd }
        ) { bookmark ->
            key(bookmark.url) {
                BookmarkTableView(
                    bookmark,
                    modifier = Modifier.padding(topBottom = 4.px, leftRight = 8.px).width(100.percent - 16.px)
                        .thenIf(model.selectedBookmarks.contains(bookmark.url), Modifier.primaryColors())
                        .userSelect(UserSelect.None)
                        .thenIf(bookmark.comment.isNotBlank(), Modifier.title(bookmark.comment))
                        .attrsModifier {
                            onClick { event ->
                                model.selectBookmark(bookmark, event.ctrlKey, event.shiftKey)
                            }
                            onDoubleClick { model.openBookmark(bookmark) }
                        },
                )
            }
        }

        Row(modifier = Modifier.width(100.percent - 32.px).padding(leftRight = 16.px, topBottom = 4.px).gap(8.px)) {
            Toggle(model.multiSelectMode, "Multi-select mode", onToggle = { model.toggleMultiSelectMode(it) })
            SpanText("or use Ctrl and Shift keys while selecting")
            Spacer()
            RowButton(onClick = { exportDialogVisible = true }) {
                Text("Import/Export")
            }
        }
    }
}