package ui.popup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import data.BookmarkRepository
import data.TabsRepository
import entity.Bookmark
import entity.Loadable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class PopupBaseModel(
    private val scope: CoroutineScope,
    private val bookmarkRepository: BookmarkRepository,
    private val tabsRepository: TabsRepository,
) {
    var state by mutableStateOf(
        PopupUiState(
            bookmark = Loadable.Loading(),
        )
    )
        private set

    init {
        scope.launch {
            state = try {
                val result = bookmarkRepository.loadBookmarkForActiveTab()
                state.copy(
                    bookmark = Loadable.Success(result),
                )
            } catch (e: Exception) {
                e.printStackTrace()
                state.copy(bookmark = Loadable.Error(e))
            }
        }
    }

    fun openManager() {
        scope.launch {
            tabsRepository.openManager()
        }
    }

    fun replaceBookmark(newBookmark: Bookmark) {
        state = state.copy(
            bookmark = Loadable.Success(newBookmark)
        )
    }
}

data class PopupUiState(
    val bookmark: Loadable<Bookmark>
)