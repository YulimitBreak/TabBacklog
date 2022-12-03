package ui.page.editor

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import common.safeCast
import data.BookmarkRepository
import entity.BookmarkType
import entity.EditedBookmark
import entity.SingleBookmarkSource
import entity.core.Loadable
import entity.core.load
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ui.common.bookmark.TimerEditorEvent
import ui.page.tagedit.TagEditEvent
import ui.page.tagedit.apply

class BookmarkEditorModel(
    private val target: SingleBookmarkSource,
    private val scope: CoroutineScope,
    private val bookmarkRepository: BookmarkRepository,
    onNavigateBackState: State<OnNavigateBack>,
) {

    private val onNavigateBack by onNavigateBackState

    var bookmark: Loadable<EditedBookmark> by mutableStateOf(Loadable.Loading())
        private set

    var editedBlock: BookmarkEditedBlock? by mutableStateOf(null)
        private set

    private val reminderDelegate = TimerEditorDelegate { bookmark.value?.remindDate }
    private val deadlineDelegate = TimerEditorDelegate { bookmark.value?.deadline }
    private val expirationDelegate = TimerEditorDelegate { bookmark.value?.expirationDate }
    private fun timerDelegate(type: TimerType) = when (type) {
        TimerType.REMINDER -> reminderDelegate
        TimerType.DEADLINE -> deadlineDelegate
        TimerType.EXPIRATION -> expirationDelegate
    }

    init {
        scope.load(
            setter = { bookmark = it },
        ) {
            when (target) {
                SingleBookmarkSource.CurrentTab -> bookmarkRepository.loadBookmarkForActiveTab()
                is SingleBookmarkSource.SelectedBookmark -> target.bookmark
                is SingleBookmarkSource.Url -> bookmarkRepository.loadBookmark(target.url)
                    ?: throw IllegalStateException("Bookmark Not Found")
            }.let {
                EditedBookmark(it)
            }
        }

    }

    private fun updateBookmark(action: (EditedBookmark) -> EditedBookmark) {
        val bookmark = bookmark.value ?: kotlin.run {
            console.warn("Updating bookmark in unloaded state")
            return
        }
        this.bookmark = Loadable.Success(action(bookmark))
    }

    fun updateFavorite(favorite: Boolean) {
        updateBookmark { it.copy(favorite = favorite) }
    }

    fun updateTitle(title: String) {
        updateBookmark { it.copy(title = title) }
    }

    fun updateComment(comment: String) {
        updateBookmark { it.copy(comment = comment) }
    }

    fun deleteBookmark() {
        scope.launch {
            val url = bookmark.value?.base?.url
                ?: target.safeCast<SingleBookmarkSource.Url>()?.url
                ?: kotlin.run {
                    console.warn("Deleting bookmark in unloaded state")
                    return@launch
                }
            bookmark = Loadable.Loading()
            bookmarkRepository.deleteBookmark(url)
            onNavigateBack()
        }
    }

    fun updateType(type: BookmarkType) {
        updateBookmark { it.copy(currentType = type) }
    }

    fun onTagEvent(event: TagEditEvent) {
        updateBookmark { bookmark -> bookmark.copy(tags = event.apply(bookmark.tags)) }
    }

    fun getDatePickerTarget(type: TimerType) = timerDelegate(type).datePickerTarget

    fun onTimerEvent(timerType: TimerType, event: TimerEditorEvent) {
        timerDelegate(timerType).onTimerEvent(event)
        if (event is TimerEditorEvent.OnDelete) {
            editedBlock = null
        }
    }

    fun requestEdit(block: BookmarkEditedBlock?) {
        editedBlock = block
    }

    fun saveBookmark() {
        scope.launch {
            var editedBookmark = bookmark.value ?: kotlin.run {
                console.warn("Saving bookmark in unloaded state")
                return@launch
            }
            reminderDelegate.applyTimer { editedBookmark = editedBookmark.copy(remindDate = it) }
            deadlineDelegate.applyTimer { editedBookmark = editedBookmark.copy(deadline = it) }
            expirationDelegate.applyTimer { editedBookmark = editedBookmark.copy(expirationDate = it) }
            bookmark = Loadable.Loading()
            bookmarkRepository.saveBookmark(editedBookmark.toImmutableBookmark())
            onNavigateBack()
        }
    }

    fun interface OnNavigateBack {
        operator fun invoke()
    }

}