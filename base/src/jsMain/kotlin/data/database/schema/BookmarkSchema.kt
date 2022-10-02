package data.database.schema

import data.database.core.DbField.Index
import data.database.core.EntityDbField
import entity.Bookmark

@Suppress("unused")
enum class BookmarkSchema(
    override val backingField: ((Bookmark) -> dynamic)? = null,
    override val index: Index? = null,
) : EntityDbField<Bookmark> {
    Url(Bookmark::url, Index.PrimaryKey),
    Title(Bookmark::title, Index.Field()),
    Favicon(Bookmark::favicon),
    Type(Bookmark::type, Index.Field()),
    CreationDate(Bookmark::creationDate, Index.Field()),
    Deadline(Bookmark::deadline, Index.Field()),
    RemindDate(Bookmark::remindDate, Index.Field()),
    ExpirationDate(Bookmark::expirationDate, Index.Field()),
    Favorite(Bookmark::favorite, Index.Field()),
    Comment(Bookmark::comment)
    ;

    override val storeName: String = "bookmarks"
}