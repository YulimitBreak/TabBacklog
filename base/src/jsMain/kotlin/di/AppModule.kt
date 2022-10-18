package di

import dev.shustoff.dikt.Create
import dev.shustoff.dikt.UseModules
import entity.core.Url
import kotlinx.coroutines.CoroutineScope
import ui.page.editor.BookmarkEditorModel
import ui.page.summary.BookmarkSummaryModel
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
    fun createBookmarkEditorModel(coroutineScope: CoroutineScope, url: Url?): BookmarkEditorModel

    @Create
    fun createBookmarkSummaryModel(coroutineScope: CoroutineScope, url: Url?): BookmarkSummaryModel

    @Create
    fun createTagEditModel(coroutineScope: CoroutineScope): TagEditModel

    @Create
    fun createBookmarkListModel(coroutineScope: CoroutineScope)
}