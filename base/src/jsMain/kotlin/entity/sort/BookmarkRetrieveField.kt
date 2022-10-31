package entity.sort

import entity.Bookmark
import entity.BookmarkType
import entity.retrieve.RetrieveField
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

sealed interface BookmarkRetrieveField<R> : RetrieveField<Bookmark, R> {
    object Url : BookmarkRetrieveField<String>
    object Title : BookmarkRetrieveField<Title>
    object Type : BookmarkRetrieveField<BookmarkType>
    object CreationDate : BookmarkRetrieveField<LocalDateTime>
    object RemindDate : BookmarkRetrieveField<LocalDate>
    object Deadline : BookmarkRetrieveField<LocalDate>
    object ExpirationDate : BookmarkRetrieveField<LocalDate>
    object Favorite : BookmarkRetrieveField<Boolean>
}