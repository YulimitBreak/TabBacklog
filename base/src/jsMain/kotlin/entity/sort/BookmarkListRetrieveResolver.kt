package entity.sort

import entity.Bookmark
import entity.retrieve.ListRetrieveResolver
import entity.retrieve.util.selectByField

class BookmarkListRetrieveResolver(source: List<Bookmark>) :
    ListRetrieveResolver<Bookmark, BookmarkRetrieveQuery>(source) {

    override fun applyQueryToList(list: List<Bookmark>, query: BookmarkRetrieveQuery): List<Bookmark> = when (query) {
        is BookmarkRetrieveQuery.CreationDate -> list.selectByField(Bookmark::creationDate, query)
        is BookmarkRetrieveQuery.Deadline -> list.selectByField(Bookmark::deadline, query)
        is BookmarkRetrieveQuery.ExpirationDate -> list.selectByField(Bookmark::expirationDate, query)
        is BookmarkRetrieveQuery.Favorite -> list.filter { it.favorite == query.target }
        is BookmarkRetrieveQuery.RemindDate -> list.selectByField(Bookmark::remindDate, query)
        is BookmarkRetrieveQuery.Title -> list.selectByField(Bookmark::title, query)
        is BookmarkRetrieveQuery.Type -> list.filter { it.type == query.target }
        is BookmarkRetrieveQuery.Url -> list.selectByField(Bookmark::url, query)
    }
}