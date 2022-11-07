package entity.sort

import entity.Bookmark
import entity.retrieve.ListRetrieveResolver

class BookmarkListRetrieveResolver(source: List<Bookmark>) :
    ListRetrieveResolver<Bookmark, BookmarkRetrieveQuery>(source) {

    override fun applySelectToList(source: List<Bookmark>, query: BookmarkRetrieveQuery): List<Bookmark> =
        when (query) {
            is BookmarkRetrieveQuery.CreationDate -> sortListByField(source, Bookmark::creationDate, query)
            is BookmarkRetrieveQuery.Deadline -> sortListByField(source, Bookmark::deadline, query)
            is BookmarkRetrieveQuery.ExpirationDate -> sortListByField(source, Bookmark::expirationDate, query)
            is BookmarkRetrieveQuery.Favorite -> source.filter { it.favorite == query.target }
            is BookmarkRetrieveQuery.RemindDate -> sortListByField(source, Bookmark::remindDate, query)
            is BookmarkRetrieveQuery.Title -> sortListByField(source, Bookmark::title, query)
            is BookmarkRetrieveQuery.Type -> source.filter { it.type == query.target }
            is BookmarkRetrieveQuery.Url -> sortListByField(source, Bookmark::url, query)
        }

}