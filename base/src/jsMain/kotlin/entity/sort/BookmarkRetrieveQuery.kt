package entity.sort

import entity.Bookmark
import entity.BookmarkType
import entity.retrieve.RetrieveQuery
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

sealed interface BookmarkRetrieveQuery : RetrieveQuery<Bookmark> {
    data class Url(
        override val from: String? = null,
        override val to: String? = null,
        override val ascending: Boolean = true,
        override val fallbackSort: Comparator<Bookmark>? = null,
    ) :
        BookmarkRetrieveQuery, RetrieveQuery.Sort<Bookmark, String>

    data class Title(
        override val from: String? = null,
        override val to: String? = null,
        override val ascending: Boolean = true,
        override val fallbackSort: Comparator<Bookmark>? = null,
    ) :
        BookmarkRetrieveQuery, RetrieveQuery.Sort<Bookmark, String>

    data class Type(override val target: BookmarkType) : BookmarkRetrieveQuery,
        RetrieveQuery.Filter<Bookmark, BookmarkType>

    data class CreationDate(
        override val from: LocalDateTime? = null,
        override val to: LocalDateTime? = null,
        override val ascending: Boolean = true,
        override val fallbackSort: Comparator<Bookmark>? = null,
    ) : BookmarkRetrieveQuery, RetrieveQuery.Sort<Bookmark, LocalDateTime>

    data class RemindDate(
        override val from: LocalDate? = null,
        override val to: LocalDate? = null,
        override val ascending: Boolean = true,
        override val fallbackSort: Comparator<Bookmark>? = null,
    ) : BookmarkRetrieveQuery, RetrieveQuery.Sort<Bookmark, LocalDate>

    data class Deadline(
        override val from: LocalDate? = null,
        override val to: LocalDate? = null,
        override val ascending: Boolean = true,
        override val fallbackSort: Comparator<Bookmark>? = null,
    ) : BookmarkRetrieveQuery, RetrieveQuery.Sort<Bookmark, LocalDate>

    data class ExpirationDate(
        override val from: LocalDate? = null,
        override val to: LocalDate? = null,
        override val ascending: Boolean = true,
        override val fallbackSort: Comparator<Bookmark>? = null,
    ) : BookmarkRetrieveQuery, RetrieveQuery.Sort<Bookmark, LocalDate>

    data class Favorite(override val target: Boolean) : BookmarkRetrieveQuery, RetrieveQuery.Filter<Bookmark, Boolean>
}