package ui.page.export

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import data.BookmarkRepository
import data.BrowserInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import org.w3c.files.Blob
import ui.common.ext.request

class ExportDialogModel(
    private val coroutineScope: CoroutineScope,
    private val bookmarkRepository: BookmarkRepository,
    private val browserInteractor: BrowserInteractor,
    onDismissState: State<OnDismiss>
) {

    private val onDismiss by onDismissState

    var isLoading by mutableStateOf(false)
    private val _onError = MutableSharedFlow<Throwable>()
    val onError: Flow<Throwable> get() = _onError // TODO


    fun exportBookmarks() {
        coroutineScope.request(
            onLoading = ::isLoading::set,
            onError = { this._onError.tryEmit(it) }
        ) {
            bookmarkRepository.getAllBookmarks().let { browserInteractor.exportBookmarks(it) }
            onDismiss()
        }
    }

    fun importBookmarks(file: Blob) {
        coroutineScope.request(
            onLoading = ::isLoading::set,
            onError = { this._onError.tryEmit(it) }
        ) {
            val bookmarks = browserInteractor.importBookmarks(file)
            bookmarkRepository.saveAllBookmarks(bookmarks)
            onDismiss()
        }
    }


    fun interface OnDismiss {
        operator fun invoke()
    }
}