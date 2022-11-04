package repository.bookmark

import common.TestBrowserInteractor
import data.BookmarkRepository
import data.database.core.DatabaseHolder
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
abstract class BookmarkRepositoryBaseTest : BookmarkDatabaseBaseTest() {

    fun repository(holder: DatabaseHolder): BookmarkRepository =
        BookmarkRepository(
            holder,
            // TODO mocking when libraries available
            TestBrowserInteractor()
        )

}