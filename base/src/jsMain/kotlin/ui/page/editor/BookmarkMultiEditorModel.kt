package ui.page.editor

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import common.DateUtils
import common.safeCast
import data.BookmarkRepository
import entity.*
import entity.core.Loadable
import entity.core.load
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ui.common.bookmark.TimerEditorEvent
import ui.page.tagedit.TagEditEvent

class BookmarkMultiEditorModel(
    private val target: MultiBookmarkSource,
    private val scope: CoroutineScope,
    private val bookmarkRepository: BookmarkRepository,
    onNavigateBackState: State<BookmarkEditorModel.OnNavigateBack>,
) {
    private val onNavigateBack by onNavigateBackState

    var bookmarkBundle: Loadable<EditedBookmarkBundle> by mutableStateOf(Loadable.Loading())
        private set

    var editedBlock by mutableStateOf<BookmarkEditedBlock?>(null)
        private set


    private val reminderDelegate = TimerEditorDelegate { bookmarkBundle.value?.base?.earliestRemindDate }
    private val deadlineDelegate = TimerEditorDelegate { bookmarkBundle.value?.base?.earliestDeadline }
    private val expirationDelegate = TimerEditorDelegate { bookmarkBundle.value?.base?.earliestExpirationDate }
    private fun timerDelegate(type: TimerType) = when (type) {
        TimerType.REMINDER -> reminderDelegate
        TimerType.DEADLINE -> deadlineDelegate
        TimerType.EXPIRATION -> expirationDelegate
    }

    init {
        scope.load(::bookmarkBundle::set) {
            when (target) {
                is MultiBookmarkSource.Url -> target.urls.mapNotNull { url ->
                    bookmarkRepository.loadBookmark(url)
                }
            }.let {
                EditedBookmarkBundle(BookmarkBundle(it))
            }
        }

    }

    private fun updateBundle(action: (EditedBookmarkBundle) -> EditedBookmarkBundle) {
        val bookmark = bookmarkBundle.value ?: kotlin.run {
            console.warn("Updating bookmark in unloaded state")
            return
        }
        this.bookmarkBundle = Loadable.Success(action(bookmark))
    }

    fun toggleFavorite() {
        updateBundle {
            it.copy(
                favorite = when (it.favorite) {
                    true -> false
                    false -> null
                    null -> true
                }
            )
        }
    }


    fun deleteAll() {
        scope.launch {
            val urls = bookmarkBundle.value?.base?.map { it.url }
                ?: target.safeCast<MultiBookmarkSource.Url>()?.urls ?: kotlin.run {
                    console.warn("Deleting bookmark in unloaded state")
                    return@launch
                }
            bookmarkBundle = Loadable.Loading()
            urls.forEach {
                bookmarkRepository.deleteBookmark(it)
            }
            onNavigateBack()
        }
    }

    fun updateType(type: BookmarkType) {
        updateBundle { it.copy(currentType = type) }
    }

    fun onTagEvent(event: TagEditEvent) {
        updateBundle { bundle ->
            with(bundle) {
                when (event) {
                    is TagEditEvent.Add -> when (val tag = event.tag) {
                        in tags -> this
                        in removedTags -> copy(removedTags = removedTags - tag)
                        else -> copy(addedTags = addedTags + tag)
                    }

                    is TagEditEvent.Delete -> when (val tag = event.tag) {
                        in coreTags -> copy(removedTags = removedTags + tag)
                        in addedTags -> copy(addedTags = addedTags - tag)
                        else -> this
                    }

                    is TagEditEvent.Edit -> {
                        when {
                            event.from in addedTags && event.to !in addedTags && event.to !in removedTags ->
                                copy(addedTags = addedTags - event.from + event.to)

                            event.from in addedTags -> copy(
                                addedTags = addedTags - event.from,
                                removedTags = removedTags - event.to
                            )

                            event.from in coreTags + offTags && event.to !in addedTags -> copy(
                                addedTags = addedTags + event.to,
                                removedTags = removedTags + event.from
                            )

                            event.from in coreTags + offTags -> copy(
                                removedTags = removedTags + event.from
                            )

                            event.to !in addedTags -> copy(addedTags = addedTags + event.to)
                            else -> this
                        }
                    }
                }
            }
        }
    }

    fun getTimerTarget(type: TimerType) = timerDelegate(type).timerTarget

    fun onTimerEvent(timerType: TimerType, event: TimerEditorEvent) {
        // TODO handle undefined
        timerDelegate(timerType).onTimerEvent(event)
        if (event is TimerEditorEvent.OnDelete) {
            editedBlock = null
        }
    }

    fun requestEdit(block: BookmarkEditedBlock?) {
        editedBlock = block
    }

    fun saveAll() {
        scope.launch {
            val bundle = bookmarkBundle.value ?: return@launch
            bookmarkBundle = Loadable.Loading()
            bundle.base.forEach { source ->
                var dest = source.copy(
                    tags = ((source.tags - bundle.removedTags) + bundle.addedTags).distinct()
                )
                if (bundle.currentType != null) {
                    dest = dest.copy(type = bundle.currentType)
                }
                if (bundle.favorite != null) {
                    dest = dest.copy(favorite = bundle.favorite)
                }
                if (!bundle.reminderUnset) {
                    reminderDelegate.applyTimer { dest = dest.copy(remindDate = it) }
                }
                if (!bundle.deadlineUnset) {
                    deadlineDelegate.applyTimer { dest = dest.copy(deadline = it) }
                }
                if (!bundle.expirationUnset) {
                    expirationDelegate.applyTimer { dest = dest.copy(expirationDate = it) }
                }
                dest.save()
            }
            onNavigateBack()
        }
    }

    private suspend fun Bookmark.save() {
        if (this.isSaved) {
            bookmarkRepository.saveBookmark(this)
        } else {
            bookmarkRepository.saveBookmark(copy(creationDate = DateUtils.now))
        }
    }

}