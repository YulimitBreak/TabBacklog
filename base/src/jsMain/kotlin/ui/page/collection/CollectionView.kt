package ui.page.collection

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.width
import org.jetbrains.compose.web.css.px
import ui.common.bookmark.CombinedBookmarkView
import ui.manager.ManagerLayout
import ui.page.bookmarklist.BookmarkList

@Composable
fun CollectionView(modifier: Modifier = Modifier) {

    var selectedBookmarkUrls by remember { mutableStateOf(emptySet<String>()) }
    var editMode by remember { mutableStateOf(false) }

    ManagerLayout(modifier,
        searchBlock = { m ->
            BookmarkList(
                modifier = m,
                onBookmarkSelect = {
                    editMode = false
                    selectedBookmarkUrls = it
                }
            )
        },
        editBlock = { m ->
            CombinedBookmarkView(
                selectedBookmarkUrls,
                editMode,
                m.width(400.px),
                onChangeEditMode = { editMode = it }
            )
        }
    )
}