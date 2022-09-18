package di

import data.BookmarkRepository
import data.TabRepository
import dev.shustoff.dikt.Create

@Suppress("unused")
class AppModule {

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