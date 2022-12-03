package ui.page.collection

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.silk.components.icons.fa.FaFileArrowUp
import data.BrowserInteractor
import entity.MultiBookmarkSource
import entity.SingleBookmarkSource
import org.jetbrains.compose.web.dom.Text
import ui.common.basecomponent.RowButton
import ui.page.editor.BookmarkEditor
import ui.page.editor.BookmarkMultiEditor
import ui.page.summary.BookmarkMultiSummary
import ui.page.summary.BookmarkSummary

@Composable
fun CollectionEditView(
    selectedBookmarkUrls: Set<String>,
    editMode: Boolean,
    modifier: Modifier = Modifier,
    onChangeEditMode: (Boolean) -> Unit
) {
    if (selectedBookmarkUrls.size == 1) {
        val bookmarkUrl = selectedBookmarkUrls.single()

        if (!editMode) {
            BookmarkSummary(
                target = SingleBookmarkSource.Url(bookmarkUrl),
                modifier = modifier,
                firstButton = {
                    val browserInteractor = BrowserInteractor.Local.current
                    RowButton(onClick = { browserInteractor.openPage(bookmarkUrl) }) {
                        FaFileArrowUp()
                        Text("Open")
                    }
                },
                onEditRequest = { onChangeEditMode(true) })
        } else {
            BookmarkEditor(
                target = SingleBookmarkSource.Url(bookmarkUrl),
                modifier = modifier,
                onNavigateBack = { onChangeEditMode(false) }
            )
        }
    } else if (selectedBookmarkUrls.size > 1) {
        if (!editMode) {
            BookmarkMultiSummary(
                target = MultiBookmarkSource.Url(selectedBookmarkUrls),
                modifier = modifier,
                firstButton = {
                    val browserInteractor = BrowserInteractor.Local.current
                    RowButton(onClick = { browserInteractor.openPages(selectedBookmarkUrls.toList()) }) {
                        FaFileArrowUp()
                        Text("Open all")
                    }
                },
                onEditRequest = { onChangeEditMode(true) }
            )
        } else {
            BookmarkMultiEditor(
                target = MultiBookmarkSource.Url(selectedBookmarkUrls),
                modifier = modifier,
                onNavigateBack = { onChangeEditMode(false) }
            )
        }
    }
}