package di

import dev.shustoff.dikt.Create
import dev.shustoff.dikt.UseModules
import entity.Bookmark
import entity.Url
import kotlinx.coroutines.CoroutineScope
import ui.page.editor.OldBookmarkEditorModel
import ui.page.summary.BookmarkSummaryModel
import ui.popup.PopupBaseModel

@Suppress("unused", "UNUSED_PARAMETER")
@UseModules(RepositoryModule::class)
class AppModule(
    val repositoryModule: RepositoryModule
) {

    @Create
    fun createPopupBaseModel(coroutineScope: CoroutineScope): PopupBaseModel

    @Create
    fun createBookmarkEditorModel(coroutineScope: CoroutineScope, baseBookmark: Bookmark): OldBookmarkEditorModel

    @Create
    fun createBookmarkSummaryModel(
        coroutineScope: CoroutineScope,
        url: Url?
    ): BookmarkSummaryModel
}