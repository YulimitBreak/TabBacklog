package data.database.schema

import data.database.core.DbField.Index
import data.database.core.DbSchema
import data.database.core.EntityDbField
import data.database.core.saveAsString
import entity.Bookmark
import entity.BookmarkType
import kotlinx.datetime.toLocalDate
import kotlinx.datetime.toLocalDateTime

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
    RemindDate(saveAsString(Bookmark::remindDate), Index.Field()),
    Deadline(saveAsString(Bookmark::deadline), Index.Field()),
    ExpirationDate(saveAsString(Bookmark::expirationDate), Index.Field()),
    Favorite(Bookmark::favorite, Index.Field()),
    Comment(Bookmark::comment)
    ;

    override val storeName: String = "bookmarks"
}

fun DbSchema<BookmarkSchema>.extractObject(source: dynamic) = extract(source) {
    Bookmark(
        BookmarkSchema.Url.value(),
        BookmarkSchema.Title.value(),
        BookmarkSchema.Favicon.value(),
        BookmarkSchema.Type.value<String>().let { BookmarkType.valueOf(it) },
        BookmarkSchema.CreationDate.value<String?>()?.toLocalDateTime(),
        BookmarkSchema.RemindDate.value<String?>()?.toLocalDate(),
        BookmarkSchema.Deadline.value<String?>()?.toLocalDate(),
        BookmarkSchema.ExpirationDate.value<String?>()?.toLocalDate(),
        favorite = BookmarkSchema.Favorite.value(),
        comment = BookmarkSchema.Comment.value() ?: ""
    )
}