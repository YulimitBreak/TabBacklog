package ui.popup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import data.BookmarkRepository
import data.TabRepository
import entity.EditedBookmark
import entity.Loadable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class PopupModel(
    private val scope: CoroutineScope,
    private val bookmarkRepository: BookmarkRepository,
    private val tabRepository: TabRepository,
) {
    private var state by mutableStateOf(
        PopupState(
            bookmark = Loadable.Loading(),
        )
    )

    val uiState get() = state.toUiState()

    init {
        scope.launch {
            state = try {
                val result = bookmarkRepository.loadBookmarkForActiveTab()
                state.copy(
                    bookmark = Loadable.Success(EditedBookmark(result)),
                )
            } catch (e: Exception) {
                state.copy(bookmark = Loadable.Error(e))
            }
        }
    }

    fun openManager() {
        scope.launch {
            tabRepository.openManager()
        }
    }

    fun updateBookmark(editedBookmark: EditedBookmark) {
        state = state.copy(bookmark = Loadable.Success(editedBookmark))
    }
}

private data class PopupState(
    val bookmark: Loadable<EditedBookmark>,
) {
    fun toUiState() = PopupUiState(
        bookmark
    )
}

data class PopupUiState(
    val bookmark: Loadable<EditedBookmark>
)