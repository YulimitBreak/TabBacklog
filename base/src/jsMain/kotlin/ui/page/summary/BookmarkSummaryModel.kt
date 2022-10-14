package ui.page.summary

import data.BookmarkRepository
import data.TagRepository
import kotlinx.coroutines.CoroutineScope

class BookmarkSummaryModel(
    private val scope: CoroutineScope,
    private val bookmarkRepository: BookmarkRepository,
    private val tagRepository: TagRepository,
) {
}