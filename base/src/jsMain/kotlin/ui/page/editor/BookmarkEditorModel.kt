package ui.page.editor

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import common.safeCast
import data.BookmarkRepository
import entity.BookmarkType
import entity.EditedBookmark
import entity.SingleBookmarkTarget
import entity.core.Loadable
import entity.core.load
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import ui.common.datepicker.DatePickerMode
import ui.common.datepicker.DatePickerTarget
import ui.page.tagedit.TagEditEvent

class BookmarkEditorModel(
    private val target: SingleBookmarkTarget,
    private val scope: CoroutineScope,
    private val bookmarkRepository: BookmarkRepository,
    onNavigateBackState: State<OnNavigateBack>,
) {

    private val onNavigateBack by onNavigateBackState

    var bookmark: Loadable<EditedBookmark> by mutableStateOf(Loadable.Loading())
        private set

    var editedBlock: BookmarkEditedBlock? by mutableStateOf(null)
        private set

    private val timerStates = mapOf(
        TimerType.REMINDER to mutableStateOf<TimerState?>(null),
        TimerType.DEADLINE to mutableStateOf<TimerState?>(null),
        TimerType.EXPIRATION to mutableStateOf<TimerState?>(null),
    )

    init {
        scope.load(
            setter = { bookmark = it },
        ) {
            when (target) {
                SingleBookmarkTarget.CurrentTab -> bookmarkRepository.loadBookmarkForActiveTab()
                is SingleBookmarkTarget.SelectedBookmark -> target.bookmark
                is SingleBookmarkTarget.Url -> bookmarkRepository.loadBookmark(target.url)
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
                ?: target.safeCast<SingleBookmarkTarget.Url>()?.url
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
        updateBookmark { bookmark ->
            when (event) {
                is TagEditEvent.Add -> {
                    if (bookmark.tags.contains(event.tag)) {
                        bookmark
                    } else {
                        bookmark.copy(tags = bookmark.tags + event.tag)
                    }
                }

                is TagEditEvent.Delete -> {
                    bookmark.copy(tags = bookmark.tags - event.tag)
                }

                is TagEditEvent.Edit -> {
                    if (bookmark.tags.contains(event.to)) {
                        bookmark.copy(tags = bookmark.tags - event.from)
                    } else {
                        bookmark.copy(tags = bookmark.tags - event.from + event.to)
                    }
                }
            }
        }
    }

    private fun getTimerState(timerType: TimerType): TimerState {
        val state = timerStates.getValue(timerType)
        state.value?.let { return it }
        fun generateState(date: LocalDate?): TimerState =
            if (date != null) {
                TimerState(date, 1, DatePickerMode.SET)
            } else {
                TimerState(null, 1, DatePickerMode.NONE)
            }

        return generateState(
            when (timerType) {
                TimerType.REMINDER -> bookmark.value?.remindDate
                TimerType.DEADLINE -> bookmark.value?.deadline
                TimerType.EXPIRATION -> bookmark.value?.expirationDate
            }
        )
    }

    fun getTimerTarget(type: TimerType) = getTimerState(type).toRelativeTimerTarget()

    fun onTimerEvent(timerType: TimerType, event: TimerEditorEvent) {
        val state = getTimerState(timerType)
        timerStates.getValue(timerType).value = when (event) {
            is TimerEditorEvent.OnCountChange -> state.copy(count = event.count.coerceAtLeast(1))
            is TimerEditorEvent.OnDateSelect -> state.copy(rememberedDate = event.date)
            is TimerEditorEvent.OnDelete -> {
                state.copy(rememberedDate = null, selectedMode = DatePickerMode.NONE).also {
                    editedBlock = null
                }
            }

            is TimerEditorEvent.OnModeChange -> state.copy(selectedMode = event.mode)
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
            timerStates[TimerType.REMINDER]?.value?.let {
                editedBookmark = editedBookmark.copy(remindDate = it.toRelativeTimerTarget().resolve())
            }
            timerStates[TimerType.DEADLINE]?.value?.let {
                editedBookmark = editedBookmark.copy(deadline = it.toRelativeTimerTarget().resolve())
            }
            timerStates[TimerType.EXPIRATION]?.value?.let {
                editedBookmark = editedBookmark.copy(expirationDate = it.toRelativeTimerTarget().resolve())
            }
            bookmark = Loadable.Loading()
            bookmarkRepository.saveBookmark(editedBookmark.toImmutableBookmark())
            onNavigateBack()
        }
    }

    fun interface OnNavigateBack {
        operator fun invoke()
    }

}

enum class BookmarkEditedBlock {
    TITLE,
    COMMENT,
    TAGS,
    REMINDER,
    DEADLINE,
    EXPIRATION,
}

enum class TimerType(val block: BookmarkEditedBlock) {
    REMINDER(BookmarkEditedBlock.REMINDER),
    DEADLINE(BookmarkEditedBlock.DEADLINE),
    EXPIRATION(BookmarkEditedBlock.EXPIRATION),
}

private data class TimerState(
    val rememberedDate: LocalDate?,
    val count: Int,
    val selectedMode: DatePickerMode,
) {
    fun toRelativeTimerTarget() = when (selectedMode) {
        DatePickerMode.NONE -> DatePickerTarget.None
        DatePickerMode.SET -> DatePickerTarget.SetDate(rememberedDate)
        DatePickerMode.DAYS -> DatePickerTarget.Counter.Days(count)
        DatePickerMode.WEEKS -> DatePickerTarget.Counter.Weeks(count)
        DatePickerMode.MONTHS -> DatePickerTarget.Counter.Months(count)
        DatePickerMode.YEARS -> DatePickerTarget.Counter.Years(count)
    }
}