package entity.sort

import entity.Bookmark
import entity.retrieve.ListRetrieveResolver
import entity.retrieve.util.selectByField

class BookmarkListRetrieveResolver(source: List<Bookmark>) :
    ListRetrieveResolver<Bookmark, BookmarkRetrieveQuery>(source) {

    override fun applyQueryToList(list: List<Bookmark>, query: BookmarkRetrieveQuery): List<Bookmark> = when (query) {
        is BookmarkRetrieveQuery.CreationDate -> source.selectByField(Bookmark::creationDate, query)
        is BookmarkRetrieveQuery.Deadline -> source.selectByField(Bookmark::deadline, query)
        is BookmarkRetrieveQuery.ExpirationDate -> source.selectByField(Bookmark::expirationDate, query)
        is BookmarkRetrieveQuery.Favorite -> source.filter { it.favorite == query.target }
        is BookmarkRetrieveQuery.RemindDate -> source.selectByField(Bookmark::remindDate, query)
        is BookmarkRetrieveQuery.Title -> source.selectByField(Bookmark::title, query)
        is BookmarkRetrieveQuery.Type -> source.filter { it.type == query.target }
        is BookmarkRetrieveQuery.Url -> source.selectByField(Bookmark::url, query)
    }
}