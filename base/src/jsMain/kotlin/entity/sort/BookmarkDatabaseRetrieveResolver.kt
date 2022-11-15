package entity.sort

import com.juul.indexeddb.Database
import data.database.core.DbSchema
import data.database.schema.BookmarkSchema
import data.database.schema.extractObject
import data.database.util.DatabaseBookmarkScope
import entity.Bookmark
import entity.retrieve.DatabaseRetrieveResolver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter

class BookmarkDatabaseRetrieveResolver(database: suspend () -> Database) :
    DatabaseRetrieveResolver<Bookmark, BookmarkRetrieveQuery>(database), DatabaseBookmarkScope {

    private val schema = DbSchema<BookmarkSchema>()

    override val storeName: String = schema.storeName

    override fun extract(source: dynamic): Bookmark = schema.extractObject(source)

    override suspend fun resolveQuery(query: BookmarkRetrieveQuery): DatabaseQuery<Bookmark> = when (query) {
        is BookmarkRetrieveQuery.CreationDate ->
            DatabaseQuery(BookmarkSchema.CreationDate.name, Bookmark::creationDate, query) { it.toString() }

        is BookmarkRetrieveQuery.Deadline ->
            DatabaseQuery(BookmarkSchema.Deadline.name, Bookmark::deadline, query) { it.toString() }

        is BookmarkRetrieveQuery.ExpirationDate ->
            DatabaseQuery(BookmarkSchema.ExpirationDate.name, Bookmark::expirationDate, query) { it.toString() }

        is BookmarkRetrieveQuery.Favorite ->
            DatabaseQuery(BookmarkSchema.Favorite.name, query)

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
        data.copy(tags = database().transaction(tagsSchema.storeName) {
            getTagsTransaction(data.url, withSorting = true)
        })
}