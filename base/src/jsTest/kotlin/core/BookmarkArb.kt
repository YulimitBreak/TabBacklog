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

fun bookmarkArb(
    title: Arb<String> = Arb.string(),
    url: Arb<String> = Arb.domain().map { "http://$it" },
    favicon: Arb<String?> = Arb.domain().map { "http://$it" }.orNull(0.1),
    type: Arb<BookmarkType> = Arb.enum(),
    creationDate: Arb<LocalDateTime?> = Arb.datetime().toLocalDateTime(),
    remindDate: Arb<LocalDate?> = Arb.date().toLocalDate().orNull(0.5),
    deadline: Arb<LocalDate?> = Arb.date().toLocalDate().orNull(0.8),
    expirationDate: Arb<LocalDate?> = Arb.int(min = 0, max = 365 * 10).map { days ->
        DateUtils.today + DatePeriod(days = days)
    }.orNull(0.9),
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

fun bookmarkArbShort() = bookmarkArb(
    title = Arb.string(0..10, Codepoint.alphanumeric()),
    url = Arb.string(0..10, Codepoint.az()).map { "http://$it.com" },
    favicon = Arb.string(0..10, Codepoint.az()).map { "http://$it.com" }.orNull(nullProbability = 0.1),
    comment = Arb.string(0..10, Codepoint.alphanumeric())
)

fun Arb<Date>.toLocalDate() = map { it.toLocalDate() }

fun Arb<Date>.toLocalDateTime() = map { it.toLocalDateTime() }

fun Date.toLocalDate() = LocalDate(getFullYear(), getMonth(), getDate())

fun Date.toLocalDateTime() =
    LocalDateTime(getFullYear(), getMonth(), this.getDate(), getHours(), getMinutes(), getSeconds())