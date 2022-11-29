package ui.page.editor

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import data.BookmarkRepository
import entity.EditedBookmarkBundle
import entity.MultiBookmarkSource
import entity.core.Loadable
import kotlinx.coroutines.CoroutineScope

class BookmarkMultiEditorModel(
    private val target: MultiBookmarkSource,
    private val scope: CoroutineScope,
    private val bookmarkRepository: BookmarkRepository,
    onNavigateBackState: State<BookmarkEditorModel.OnNavigateBack>,
) {
    private val onNavigateBack by onNavigateBackState

    var bookmark: Loadable<EditedBookmarkBundle> by mutableStateOf(Loadable.Loading())
        private set
}