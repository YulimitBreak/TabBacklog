package di

import data.BookmarkRepository
import data.TabRepository
import dev.shustoff.dikt.Create


@Suppress("unused", "UNUSED_PARAMETER")
class RepositoryModule {

    // Doing it like this because @CreateSingle causes compilation issues
    val bookmarkRepository by lazy {
        provideBookmarkRepository()
    }

    @Create
    private fun provideBookmarkRepository(): BookmarkRepository

    val tabRepository by lazy {
        provideTabsRepository()
    }

    @Create
    private fun provideTabsRepository(): TabRepository
}