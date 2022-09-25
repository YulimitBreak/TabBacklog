package ui.common.bookmark.editor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import data.BookmarkRepository
import data.TagRepository
import entity.Bookmark
import entity.BookmarkType
import entity.EditedBookmark
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class BookmarkEditorModel(
    private val baseBookmark: Bookmark,
    private val scope: CoroutineScope,
    private val bookmarkRepository: BookmarkRepository,
    private val tagRepository: TagRepository,
) {

    var state by mutableStateOf(
        UiState(
            bookmark = EditedBookmark(baseBookmark),
            tagInputUiState = TagInputState(),
            openedPanel = null
        )
    )
        private set

    private fun update(
        bookmark: ((EditedBookmark) -> EditedBookmark)? = null,
        tagInput: ((TagInputState) -> TagInputState)? = null,
    ) {
        state = state.copy(
            bookmark = bookmark?.invoke(state.bookmark) ?: state.bookmark,
            tagInputUiState = tagInput?.invoke(state.tagInputUiState) ?: state.tagInputUiState
        )
    }

    private fun updateBookmark(upd: (EditedBookmark) -> EditedBookmark) = update(bookmark = upd)


    fun onTitleChanged(title: String) {
        updateBookmark { it.copy(title = title) }
    }

    fun onTypeChanged(type: BookmarkType) {
        updateBookmark { it.copy(currentType = type) }
    }

    fun onCommentChanged(comment: String) {
        updateBookmark { it.copy(comment = comment) }
    }

    fun toggleOpenedPanel(panel: OpenedPanel) {
        if (state.openedPanel == panel) {
            state = state.copy(openedPanel = null)
        } else {
            state = state.copy(openedPanel = panel)
        }
    }


    fun updateTagInput(input: String) {
        update(tagInput = {
            it.copy(currentInput = input.lowercase())
        })
        fetchTagAutocomplete(input)
    }

    fun onTagConfirm(tag: String) {
        update(
            bookmark = { bookmark ->
                if (tag in bookmark.tags) return@update bookmark
                bookmark.copy(tags = bookmark.tags + tag)
            },
            tagInput = {
                it.copy(currentInput = "", suggestedTags = emptyList())
            })
        autocompleteFetchJob?.cancel()
    }

    fun onConfirmedTagEdited(tag: String) {
        update(
            bookmark = { bookmark ->
                bookmark.copy(tags = bookmark.tags - tag)
            },
            tagInput = {
                it.copy(currentInput = tag)
            })
        fetchTagAutocomplete(tag)
    }

    fun onConfirmedTagDeleted(tag: String) {
        update(
            bookmark = {
                it.copy(tags = it.tags - tag)
            })
    }

    private var autocompleteFetchJob: Job? = null

    private fun fetchTagAutocomplete(tag: String) {
        autocompleteFetchJob?.cancel()
        autocompleteFetchJob = scope.launch {
            val tags = tagRepository.fetchTagAutocomplete(tag)
            if (!isActive) return@launch
            update(
                tagInput = { it.copy(suggestedTags = tags) }
            )
        }

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

    data class UiState(
        val bookmark: EditedBookmark,
        val tagInputUiState: TagInputState,
        val openedPanel: OpenedPanel?,
    )

    data class TagInputState(
        val currentInput: String = "",
        val suggestedTags: List<String> = emptyList(),
    )

    enum class OpenedPanel {
        TAGS,
        TIMERS,
    }
}



