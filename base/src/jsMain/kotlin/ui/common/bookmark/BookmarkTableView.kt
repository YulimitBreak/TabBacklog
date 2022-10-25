package ui.common.bookmark

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.silk.components.icons.fa.FaBookmark
import com.varabyte.kobweb.silk.components.icons.fa.FaStar
import com.varabyte.kobweb.silk.components.icons.fa.IconStyle
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.components.text.SpanText
import common.DateUtils
import common.isAfterToday
import common.js.ResizeObserver
import common.styleProperty
import entity.Bookmark
import entity.BookmarkType
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.web.css.*
import ui.common.basecomponent.DivText
import ui.styles.brand.DeadlineTimerIcon
import ui.styles.brand.ExpirationTimerIcon
import ui.styles.brand.ReminderTimerIcon
import ui.styles.components.TagComponent

@Composable
fun BookmarkTableView(
    bookmark: Bookmark,
    tagModifier: @Composable (tag: String) -> Modifier = { Modifier },
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.gap(8.px).flexWrap(FlexWrap.Nowrap), verticalAlignment = Alignment.CenterVertically) {
        Favicon(bookmark.favicon, 24.px)
        Column(
            Modifier.width(100.percent - 8.px).gap(4.px),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {

            var rowWidth by remember { mutableStateOf(0) }
            Row(
                Modifier.width(100.percent).flexWrap(FlexWrap.Nowrap).gap(4.px),
                elementScope = {
                    DisposableEffect(this) {
                        val resizeObserver = ResizeObserver {
                            rowWidth = scopeElement.clientWidth
                        }
                        resizeObserver.observe(scopeElement)
                        onDispose {
                            resizeObserver.disconnect()
                        }
                    }
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (bookmark.type == BookmarkType.LIBRARY) {
                    FaBookmark(Modifier.fontSize(1.2.em), IconStyle.FILLED)
                }
                DivText(
                    bookmark.title,
                    modifier = Modifier.title(bookmark.title)
                        .overflowWrap(OverflowWrap.Anywhere).overflow(Overflow.Hidden)
                        .styleProperty("text-overflow", "ellipsis")
                        .whiteSpace(WhiteSpace.NoWrap)
                        .lineHeight(1.2.em)
                        .maxHeight(1.2.em)
                        .maxWidth(
                            calculateTitleWidth(
                                rowWidth, bookmark, 4, 0.15f, 16
                            ).px - 16.px
                        )
                        .flexShrink(1)
                )
                if (bookmark.favorite) {
                    FaStar(Modifier.fontSize(1.1.em), IconStyle.FILLED)
                }
                Spacer()
                TimerView(
                    bookmark.remindDate,
                    "Reminder",
                    Modifier.minWidth(15.percent)
                        .backgroundColor(Color.green).color(Color.white)
                ) { ReminderTimerIcon() }
                TimerView(
                    bookmark.deadline,
                    "Deadline",
                    Modifier.minWidth(15.percent)
                        .thenIf(
                            bookmark.deadline?.isAfterToday() == true,
                            Modifier.backgroundColor(Color.orange).color(Color.white)
                        )
                        .thenIf(
                            bookmark.deadline?.isAfterToday() != true,
                            Modifier.backgroundColor(Color.crimson).color(Color.white)
                        )
                ) { DeadlineTimerIcon() }
                TimerView(
                    bookmark.expirationDate,
                    "Expiration",
                    Modifier.minWidth(15.percent).backgroundColor(Color.black).color(Color.white)
                ) { ExpirationTimerIcon() }
            }
            Row(
                Modifier.width(100.percent).flexWrap(FlexWrap.Nowrap)
                    .gap(2.px),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DivText(
                    bookmark.url,
                    modifier = Modifier.width(200.px).title(bookmark.url)
                        .whiteSpace(WhiteSpace.NoWrap).overflowWrap(OverflowWrap.Anywhere).overflow(Overflow.Hidden)
                        .fontWeight(FontWeight.Lighter)
                        .fontSize(0.8.em)
                        .styleProperty("text-overflow", "ellipsis")
                )
                Spacer()
                bookmark.tags.take(3).forEach { tag ->
                    key(tag) {
                        SpanText(
                            tag,
                            TagComponent.Style.toModifier().then(tagModifier(tag))
                        )
                    }
                }
            }
        }
    }
}

private fun calculateTitleWidth(
    rowWidthPx: Int,
    bookmark: Bookmark,
    gapWidthPx: Int,
    timerWidthPercent: Float,
    iconWidthPx: Int
): Int {
    var percent = 1f
    var rawMargin = 0
    val timersCount = listOfNotNull(bookmark.remindDate, bookmark.deadline, bookmark.expirationDate).size
    percent -= timerWidthPercent * timersCount
    rawMargin += gapWidthPx * timersCount
    if (bookmark.type == BookmarkType.LIBRARY) {
        rawMargin += iconWidthPx
    }
    if (bookmark.favorite) {
        rawMargin += iconWidthPx
    }
    return ((percent * rowWidthPx) - rawMargin).toInt()
}

@Composable
private fun TimerView(
    timer: LocalDate?,
    typeName: String,
    modifier: Modifier,
    icon: @Composable () -> Unit,
) {
    if (timer != null) {
        Row(
            modifier = modifier.gap(4.px)
                .title(typeName + ": " + DateUtils.formatTimeRelation(timer))
                .borderRadius(4.px)
                .minHeight(1.4.em)
                .fontSize(1.em)
                .padding(0.5.px)
                .textAlign(TextAlign.Center)
                .boxShadow(0.px, 5.px, 5.px, (-5).px, color = Colors.Black),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            icon()
            SpanText(DateUtils.Formatter.DmySlash(timer))
        }
    }
}