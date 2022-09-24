package ui.common.bookmark.editor

import data.BookmarkRepository
import entity.Bookmark
import entity.BookmarkType
import entity.EditedBookmark
import kotlinx.coroutines.CoroutineScope
import kotlinx.datetime.LocalDate

class BookmarkEditorModel(
    private val baseBookmark: Bookmark,
    private val scope: CoroutineScope,
    private val bookmarkRepository: BookmarkRepository,
) {

    var state = BookmarkEditorUiState(
        EditedBookmark(baseBookmark)
    )
        private set

    private fun updateBookmark(upd: (EditedBookmark) -> EditedBookmark) {
        state = state.copy(bookmark = upd(state.bookmark))
    }

    fun onTitleChanged(title: String) {
        updateBookmark { it.copy(title = title) }
    }

    fun onTypeChanged(type: BookmarkType) {
        updateBookmark { it.copy(currentType = type) }
    }

    fun onCommentChanged(comment: String) {
        updateBookmark { it.copy(comment = comment) }
    }

    fun onTimersChanged(
        deadline: LocalDate?,
        reminder: LocalDate?,
        expiration: LocalDate?,
    ) {
        updateBookmark {
            it.copy(
                deadline = deadline,
                reminder = reminder,
                expiration = expiration,
            )
        }
    }
}

data class BookmarkEditorUiState(
    val bookmark: EditedBookmark,
)