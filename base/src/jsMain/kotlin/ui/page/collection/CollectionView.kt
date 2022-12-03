package ui.page.collection

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
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
            CollectionEditView(
                selectedBookmarkUrls,
                editMode,
                m,
                onChangeEditMode = { editMode = it }
            )
        }
    )
}