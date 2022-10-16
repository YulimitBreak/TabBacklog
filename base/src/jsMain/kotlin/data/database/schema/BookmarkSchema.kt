package data.database.schema

import data.database.core.DbField.Index
import data.database.core.EntityDbField
import data.database.core.saveAsString
import entity.Bookmark

@Suppress("unused")
enum class BookmarkSchema(
    override val backingField: ((Bookmark) -> dynamic)? = null,
    override val index: Index? = null,
) : EntityDbField<Bookmark> {
    Url(Bookmark::url, Index.PrimaryKey),
    Title(Bookmark::title, Index.Field()),
    Favicon(Bookmark::favicon),
    Type(saveAsString(Bookmark::type), Index.Field()),
    CreationDate(saveAsString(Bookmark::creationDate), Index.Field()),
    Deadline(saveAsString(Bookmark::deadline), Index.Field()),
    RemindDate(saveAsString(Bookmark::remindDate), Index.Field()),
    ExpirationDate(saveAsString(Bookmark::expirationDate), Index.Field()),
    Favorite(Bookmark::favorite, Index.Field()),
    Comment(Bookmark::comment)
    ;

    override val storeName: String = "bookmarks"
}