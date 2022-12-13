package ui.page.collection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.width
import entity.BookmarkSource
import org.jetbrains.compose.web.css.px
import ui.common.bookmark.CombinedBookmarkView
import ui.manager.ManagerLayout
import ui.page.bookmarklist.BookmarkList

@Composable
fun CollectionView(modifier: Modifier = Modifier) {

    var selectedBookmarkSources by remember { mutableStateOf(emptySet<BookmarkSource>()) }
    var editMode by remember { mutableStateOf(false) }

    ManagerLayout(modifier,
        searchBlock = { m ->
            BookmarkList(
                modifier = m,
                onBookmarkSelect = { urls ->
                    editMode = false
                    selectedBookmarkSources = urls.mapTo(mutableSetOf()) { BookmarkSource.Url(it) }
                }
            )
        },
        editBlock = { m ->
            CombinedBookmarkView(
                selectedBookmarkSources,
                editMode,
                m.width(400.px),
                onChangeEditMode = { editMode = it }
            )
        }
    )
}