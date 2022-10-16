package ui.page.editor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import data.BookmarkRepository
import entity.EditedBookmark
import entity.Loadable
import entity.Url
import entity.load
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class BookmarkEditorModel(
    private val url: Url?,
    private val scope: CoroutineScope,
    private val bookmarkRepository: BookmarkRepository,
) {

    var bookmark: Loadable<EditedBookmark> by mutableStateOf(Loadable.Loading())
        private set

    init {
        scope.load(
            setter = { bookmark = it },
        ) {
            if (url != null) {
                bookmarkRepository.loadBookmark(url.url) ?: throw IllegalStateException("Bookmark Not Found")
            } else {
                bookmarkRepository.loadBookmarkForActiveTab()
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

    fun deleteBookmark(onDeletionComplete: () -> Unit) {
        scope.launch {
            val url = bookmark.value?.base?.url
                ?: url?.url
                ?: kotlin.run {
                    console.warn("Deleting bookmark in unloaded state")
                    return@launch
                }
            bookmark = Loadable.Loading()
            bookmarkRepository.deleteBookmark(url)
            onDeletionComplete()
        }
    }
}