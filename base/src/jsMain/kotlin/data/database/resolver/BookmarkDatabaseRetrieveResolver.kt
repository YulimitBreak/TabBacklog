package data.database.resolver

import com.juul.indexeddb.Database
import data.database.schema.BookmarkSchema
import data.database.schema.extractObject
import data.database.util.DatabaseBookmarkScope
import entity.Bookmark
import entity.retrieve.DatabaseRetrieveResolver
import entity.sort.BookmarkRetrieveQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter

class BookmarkDatabaseRetrieveResolver(database: suspend () -> Database) :
    DatabaseRetrieveResolver<Bookmark, BookmarkRetrieveQuery>(database), DatabaseBookmarkScope {

    override val storeName: String = bookmarkSchema.storeName

    override fun extract(source: dynamic): Bookmark = bookmarkSchema.extractObject(source)

    override suspend fun resolveQuery(query: BookmarkRetrieveQuery): DatabaseQuery<Bookmark> = when (query) {
        is BookmarkRetrieveQuery.CreationDate ->
            DatabaseQuery(BookmarkSchema.CreationDate.name, Bookmark::creationDate, query) { it.toString() }

        is BookmarkRetrieveQuery.Deadline ->
            DatabaseQuery(BookmarkSchema.Deadline.name, Bookmark::deadline, query) { it.toString() }

        is BookmarkRetrieveQuery.ExpirationDate ->
            DatabaseQuery(BookmarkSchema.ExpirationDate.name, Bookmark::expirationDate, query) { it.toString() }

        is BookmarkRetrieveQuery.Favorite ->
            DatabaseQuery(Bookmark::favorite, query)

        is BookmarkRetrieveQuery.RemindDate ->
            DatabaseQuery(BookmarkSchema.RemindDate.name, Bookmark::remindDate, query) { it.toString() }

        is BookmarkRetrieveQuery.Title ->
            DatabaseQuery(BookmarkSchema.Title.name, Bookmark::title, query)

        is BookmarkRetrieveQuery.Type ->
            DatabaseQuery(BookmarkSchema.Type.name, query) { it.toString() }

        is BookmarkRetrieveQuery.Url ->
            DatabaseQuery(BookmarkSchema.Url.name, Bookmark::url, query)
    }

    override suspend fun applyQueryToFlow(flow: Flow<Bookmark>, query: BookmarkRetrieveQuery): Flow<Bookmark> =
        when (query) {
            is BookmarkRetrieveQuery.CreationDate -> flow.applySelectToFlow(Bookmark::creationDate, query)
            is BookmarkRetrieveQuery.Deadline -> flow.applySelectToFlow(Bookmark::deadline, query)
            is BookmarkRetrieveQuery.ExpirationDate -> flow.applySelectToFlow(Bookmark::expirationDate, query)
            is BookmarkRetrieveQuery.Favorite -> flow.filter { it.favorite == query.target }
            is BookmarkRetrieveQuery.RemindDate -> flow.applySelectToFlow(Bookmark::remindDate, query)
            is BookmarkRetrieveQuery.Title -> flow.applySelectToFlow(Bookmark::title, query)
            is BookmarkRetrieveQuery.Type -> flow.filter { it.type == query.target }
            is BookmarkRetrieveQuery.Url -> flow.applySelectToFlow(Bookmark::url, query)
        }

    override suspend fun postFetch(data: Bookmark): Bookmark =
        data.copy(tags = database().transaction(tagsSchema.storeName, tagCountSchema.storeName) {
            getTags(data.url, withSorting = false)
        })
}