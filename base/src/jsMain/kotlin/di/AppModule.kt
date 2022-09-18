package di

import data.BookmarkRepository
import data.TabRepository
import dev.shustoff.dikt.Create

class AppModule {

    fun provideBookmarkRepository(): BookmarkRepository {
        return BookmarkRepository()
    }

    @Create
    fun provideTabsRepository(): TabRepository
}