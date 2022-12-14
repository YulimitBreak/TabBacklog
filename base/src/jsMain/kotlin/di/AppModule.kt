package di

import androidx.compose.runtime.State
import androidx.compose.runtime.staticCompositionLocalOf
import dev.shustoff.dikt.Create
import dev.shustoff.dikt.UseModules
import entity.BookmarkSource
import entity.MultiBookmarkSource
import entity.error.CompositionLocalError
import kotlinx.coroutines.CoroutineScope
import ui.page.bookmarklist.BookmarkListModel
import ui.page.editor.BookmarkEditorModel
import ui.page.editor.BookmarkMultiEditorModel
import ui.page.export.ExportDialogModel
import ui.page.summary.BookmarkMultiSummaryModel
import ui.page.summary.BookmarkSummaryModel
import ui.page.tablist.TabListModel
import ui.page.tagedit.TagEditModel
import ui.popup.PopupBaseModel

@Suppress("unused", "UNUSED_PARAMETER")
@UseModules(RepositoryModule::class)
class AppModule(
    val repositoryModule: RepositoryModule
) {

    @Create
    fun createPopupBaseModel(coroutineScope: CoroutineScope): PopupBaseModel

    @Create
    fun createBookmarkEditorModel(
        coroutineScope: CoroutineScope,
        target: BookmarkSource,
        onNavigateBackState: State<BookmarkEditorModel.OnNavigateBack>
    ): BookmarkEditorModel

    @Create
    fun createBookmarkMultiEditorModel(
        coroutineScope: CoroutineScope,
        target: MultiBookmarkSource,
        onNavigateBackState: State<BookmarkEditorModel.OnNavigateBack>
    ): BookmarkMultiEditorModel

    @Create
    fun createBookmarkSummaryModel(
        coroutineScope: CoroutineScope, target: BookmarkSource
    ): BookmarkSummaryModel

    @Create
    fun createBookmarkMultiSummaryModel(
        coroutineScope: CoroutineScope, target: MultiBookmarkSource
    ): BookmarkMultiSummaryModel

    @Create
    fun createTagEditModel(
        coroutineScope: CoroutineScope,
        onTagEditEventState: State<TagEditModel.OnTagEditEvent>,
    ): TagEditModel

    @Create
    fun createBookmarkListModel(
        coroutineScope: CoroutineScope,
        onBookmarkSelect: State<BookmarkListModel.OnBookmarkSelect>
    ): BookmarkListModel

    @Create
    fun createExportDialogModel(
        coroutineScope: CoroutineScope,
        onDismiss: State<ExportDialogModel.OnDismiss>,
    ): ExportDialogModel

    @Create
    fun createTabListModel(
        coroutineScope: CoroutineScope,
        onTabSelect: State<TabListModel.OnTabSelect>
    ): TabListModel

    companion object {
        val Local = staticCompositionLocalOf<AppModule> {
            throw CompositionLocalError("AppModule")
        }
    }
}