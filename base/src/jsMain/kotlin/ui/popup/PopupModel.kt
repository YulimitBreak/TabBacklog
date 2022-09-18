package ui.popup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import data.BookmarkRepository
import data.TabRepository
import entity.Bookmark
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class PopupModel(
    private val scope: CoroutineScope,
    private val bookmarkRepository: BookmarkRepository,
    private val tabRepository: TabRepository,
) {
    private var state by mutableStateOf(
        PopupState(
            bookmark = null
        )
    )

    init {

    }

    val uiState get() = state.toUiState()

    fun openManager() {
        scope.launch {
            tabRepository.openManager()
        }
    }
}

private class PopupState(
    val bookmark: Bookmark?,
) {

    fun toUiState(): PopupUiState =
        PopupUiState(
            bookmarkState = if (bookmark != null) {
                BookmarkState.Result(bookmark)
            } else BookmarkState.Loading
        )
}

data class PopupUiState(
    val bookmarkState: BookmarkState
)

sealed interface BookmarkState {
    object Loading : BookmarkState

    data class Result(val bookmark: Bookmark) : BookmarkState
}