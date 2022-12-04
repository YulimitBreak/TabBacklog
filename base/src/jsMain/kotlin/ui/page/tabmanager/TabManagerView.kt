package ui.page.tabmanager

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.width
import entity.BookmarkSource
import org.jetbrains.compose.web.css.px
import ui.common.bookmark.CombinedBookmarkView
import ui.manager.ManagerLayout
import ui.page.tablist.TabListView

@Composable
fun TabManagerView(modifier: Modifier = Modifier) {

    var selectedUrls by remember { mutableStateOf(emptySet<BookmarkSource>()) }
    var editMode by remember { mutableStateOf(false) }

    ManagerLayout(
        modifier,
        searchBlock = { m ->
            TabListView(m, onTabSelect = { sources ->
                selectedUrls = sources.mapTo(mutableSetOf()) { BookmarkSource.Tab(it) }
                editMode = false
            })
        },
        editBlock = { m ->
            CombinedBookmarkView(selectedUrls, editMode, m.width(400.px), onChangeEditMode = { editMode = it })
        },
    )
}