package data.entity

import entity.Bookmark
import entity.BookmarkType
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

external interface BookmarkJson {
    var url: String
    var title: String?
    var favicon: String?
    var type: String?
    var creationDate: String?
    var remindDate: String?
    var deadlineDate: String?
    var expirationDate: String?
    var tags: Array<String>?
    var favorite: Boolean?
    var comment: String?
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
fun Bookmark.toJsonEntity() = (js("{}") as BookmarkJson).also { j ->
    j.url = url
    j.title = title
    j.favicon = favicon?.takeIf { it.length < 256 }
    j.type = type.toString()
    j.creationDate = creationDate?.toString()
    j.remindDate = remindDate?.toString()
    j.deadlineDate = deadline?.toString()
    j.expirationDate = expirationDate?.toString()
    // Not convertible back, used because emptyList is an object that doesn't map to []
    j.tags = tags.toTypedArray()
    j.favorite = favorite
    j.comment = comment
}

private fun getBookmarkType(type: String?): BookmarkType {
    if (type == null) return BookmarkType.BACKLOG
    return try {
        BookmarkType.valueOf(type)
    } catch (_: Exception) {
        BookmarkType.BACKLOG
    }
}

fun BookmarkJson.toBookmark() = Bookmark(
    url,
    title ?: "",
    favicon,
    getBookmarkType(type),
    creationDate?.let(LocalDateTime::parse),
    remindDate?.let(LocalDate::parse),
    deadlineDate?.let(LocalDate::parse),
    expirationDate?.let(LocalDate::parse),
    tags?.toList() ?: emptyList(),
    favorite ?: false,
    comment ?: ""
)