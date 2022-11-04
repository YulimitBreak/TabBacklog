package entity.sort

import entity.Bookmark
import entity.retrieve.ListRetrieveResolver

class BookmarkListRetrieveResolver(source: List<Bookmark>) :
    ListRetrieveResolver<Bookmark, BookmarkRetrieveQuery>(source) {

    override fun handleSelect(source: List<Bookmark>, query: BookmarkRetrieveQuery): List<Bookmark> = when (query) {
        is BookmarkRetrieveQuery.CreationDate -> sortByField(source, Bookmark::creationDate, query)
        is BookmarkRetrieveQuery.Deadline -> sortByField(source, Bookmark::deadline, query)
        is BookmarkRetrieveQuery.ExpirationDate -> sortByField(source, Bookmark::expirationDate, query)
        is BookmarkRetrieveQuery.Favorite -> source.filter { it.favorite == query.target }
        is BookmarkRetrieveQuery.RemindDate -> sortByField(source, Bookmark::remindDate, query)
        is BookmarkRetrieveQuery.Title -> sortByField(source, Bookmark::title, query)
        is BookmarkRetrieveQuery.Type -> source.filter { it.type == query.target }
        is BookmarkRetrieveQuery.Url -> sortByField(source, Bookmark::url, query)
    }

}