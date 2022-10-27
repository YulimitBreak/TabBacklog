package core

import common.DateUtils
import entity.Bookmark
import entity.BookmarkType
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.plus
import kotlin.js.Date

fun bookmarkArbitrary(
    title: Arb<String> = Arb.string(),
    url: Arb<String> = Arb.domain().map { "http://$it" },
    favicon: Arb<String?> = Arb.domain().orNull(0.1).map { domain -> domain?.let { "http://$it" } },
    type: Arb<BookmarkType> = Arb.enum(),
    creationDate: Arb<LocalDateTime?> = Arb.datetime().map { it.toLocalDateTime() },
    remindDate: Arb<LocalDate?> = Arb.date().orNull(0.5).map { it?.toLocalDate() },
    deadline: Arb<LocalDate?> = Arb.date().orNull(0.8).map { it?.toLocalDate() },
    expirationDate: Arb<LocalDate?> = Arb.int(min = 0, max = 365 * 10).orNull(0.9).map { days ->
        days?.let { DateUtils.today + DatePeriod(days = it) }
    },
    tags: List<String> = emptyList(),
    favorite: Arb<Boolean> = Arb.boolean(),
    comment: Arb<String> = Arb.string()
) = arbitrary {
    Bookmark(
        url = url.bind(),
        title = title.bind(),
        favicon = favicon.bind(),
        type = type.bind(),
        creationDate = creationDate.bind(),
        remindDate = remindDate.bind(),
        deadline = deadline.bind(),
        expirationDate = expirationDate.bind(),
        tags = if (tags.isEmpty()) emptyList() else Arb.subsequence(tags).bind(),
        favorite = favorite.bind(),
        comment = comment.bind()
    )
}


fun Date.toLocalDate() = LocalDate(getFullYear(), getMonth(), getDate())

fun Date.toLocalDateTime() =
    LocalDateTime(getFullYear(), getMonth(), this.getDate(), getHours(), getMinutes(), getSeconds())