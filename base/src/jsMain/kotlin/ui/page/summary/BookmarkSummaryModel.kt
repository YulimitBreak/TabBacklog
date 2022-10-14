package ui.page.summary

import data.BookmarkRepository
import data.TabsRepository
import data.TagRepository
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class BookmarkSummaryModel(
    private val scope: CoroutineScope,
    private val bookmarkRepository: BookmarkRepository,
    private val tabsRepository: TabsRepository,
    private val tagRepository: TagRepository,
) {

    fun openManager() {
        scope.launch {
            tabsRepository.openManager()
            window.close()
        }
    }
}