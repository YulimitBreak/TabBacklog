package ui.common.bookmark

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.silk.components.icons.fa.FaFileArrowUp
import data.BrowserInteractor
import entity.BookmarkSource
import entity.MultiBookmarkSource
import org.jetbrains.compose.web.dom.Text
import ui.common.basecomponent.RowButton
import ui.page.editor.BookmarkEditor
import ui.page.editor.BookmarkMultiEditor
import ui.page.summary.BookmarkMultiSummary
import ui.page.summary.BookmarkSummary

@Composable
fun CombinedBookmarkView(
    selectedBookmarkSources: Set<BookmarkSource>,
    editMode: Boolean,
    modifier: Modifier = Modifier,
    onChangeEditMode: (Boolean) -> Unit
) {
    if (selectedBookmarkSources.size == 1) {
        val source = selectedBookmarkSources.single()

        if (!editMode) {
            BookmarkSummary(
                target = source,
                modifier = modifier,
                firstButton = {
                    if (source is BookmarkSource.Url) {
                        val browserInteractor = BrowserInteractor.Local.current
                        RowButton(onClick = { browserInteractor.openPage(source.url) }) {
                            FaFileArrowUp()
                            Text("Open")
                        }
                    }
                },
                onEditRequest = { onChangeEditMode(true) })
        } else {
            BookmarkEditor(
                target = source,
                modifier = modifier,
                onNavigateBack = { onChangeEditMode(false) }
            )
        }
    } else if (selectedBookmarkSources.size > 1) {
        if (!editMode) {
            BookmarkMultiSummary(
                target = MultiBookmarkSource(selectedBookmarkSources),
                modifier = modifier,
                firstButton = {
                    if (selectedBookmarkSources.all { it is BookmarkSource.Url }) {
                        val urls = selectedBookmarkSources.filterIsInstance<BookmarkSource.Url>().map { it.url }
                        val browserInteractor = BrowserInteractor.Local.current
                        RowButton(onClick = { browserInteractor.openPages(urls) }) {
                            FaFileArrowUp()
                            Text("Open all")
                        }
                    }
                },
                onEditRequest = { onChangeEditMode(true) }
            )
        } else {
            BookmarkMultiEditor(
                target = MultiBookmarkSource(selectedBookmarkSources),
                modifier = modifier,
                onNavigateBack = { onChangeEditMode(false) }
            )
        }
    }
}