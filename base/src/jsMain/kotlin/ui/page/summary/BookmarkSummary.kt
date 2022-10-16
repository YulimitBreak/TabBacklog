package ui.page.summary

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.icons.fa.*
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.SilkTheme
import common.styleProperty
import di.ModuleLocal
import entity.BookmarkType
import entity.Url
import org.jetbrains.compose.web.css.minus
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Text
import ui.common.basecomponent.DivText
import ui.common.basecomponent.LoadableView
import ui.common.basecomponent.RowButton
import ui.common.basecomponent.TagListView
import ui.common.bookmark.BookmarkTitleView
import ui.common.bookmark.TimerDisplay
import ui.common.styles.MainStyle


@Composable
fun BookmarkSummary(
    url: String? = null,
    onEditRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appModule = ModuleLocal.App.current
    val scope = rememberCoroutineScope()

    val model: BookmarkSummaryModel =
        remember(url) { appModule.createBookmarkSummaryModel(scope, url?.let { Url(it) }) }

    LoadableView(model.bookmark, modifier.minHeight(100.px)) { bookmark, m ->
        Column(m.gap(8.px).margin(bottom = 16.px)) {
            Row(Modifier.fillMaxWidth().gap(8.px)) {

                RowButton(onClick = { model.openManager() }) {
                    FaListCheck()
                    Text("Open manager")
                }

                Spacer()
                if (bookmark.isSaved) {
                    RowButton(
                        onClick = { model.updateFavorite(!bookmark.favorite) },
                    ) {
                        FaStar(style = if (bookmark.favorite) IconStyle.FILLED else IconStyle.OUTLINE)
                        Text("Favorite")
                    }
                    RowButton(onClick = { model.deleteBookmark() }) {
                        FaTrash()
                        Text("Delete")
                    }
                }
            }
            BookmarkTitleView(
                bookmark.title,
                bookmark.favicon,
                bookmark.url,
                Modifier.margin(leftRight = 8.px).width(100.percent - 16.px).height(64.px)
            )
            Row(Modifier.fillMaxWidth().gap(8.px)) {
                val currentType = bookmark.takeIf { it.isSaved }?.type
                RowButton(
                    onClick = {
                        model.updateType(BookmarkType.LIBRARY)
                    }, modifier = if (currentType == BookmarkType.LIBRARY) {
                        SelectedBookmarkTypeModifier
                    } else {
                        Modifier
                    }.width(30.percent)
                ) {
                    FaBookBookmark()
                    if (currentType == BookmarkType.LIBRARY) {
                        Text("In library")
                    } else {
                        Text("To library")
                    }
                }
                RowButton(
                    onClick = {
                        model.updateType(BookmarkType.BACKLOG)
                    },
                    modifier = if (currentType == BookmarkType.BACKLOG) {
                        SelectedBookmarkTypeModifier
                    } else {
                        Modifier
                    }.width(30.percent)
                ) {
                    FaNoteSticky()
                    if (currentType == BookmarkType.BACKLOG) {
                        Text("In backlog")
                    } else {
                        Text("To backlog")
                    }
                }

                Spacer()

                RowButton(onClick = {
                    onEditRequest()
                }) {
                    FaPencil()
                    Text("Edit")
                }
            }

            if (bookmark.comment.isNotBlank()) {
                SpanText("Comment:")
                DivText(bookmark.comment, Modifier.fontWeight(FontWeight.Lighter).margin(leftRight = 8.px))
            }

            if (bookmark.tags.isNotEmpty()) {
                SpanText("Tags:")
                TagListView(
                    bookmark.tags.toList(), Modifier.margin(leftRight = 8.px),
                )
            }

            if (bookmark.hasTimers) {
                SpanText("Timers:")
                Column(Modifier.margin(left = 8.px).width(100.percent - 8.px)) {
                    if (bookmark.remindDate != null) {
                        TimerDisplay("Reminder", bookmark.remindDate, Modifier.fillMaxWidth(),
                            onDelete = { model.deleteReminder() }
                        )
                    }
                    if (bookmark.deadline != null) {
                        TimerDisplay("Deadline", bookmark.deadline, Modifier.fillMaxWidth(),
                            onDelete = { model.deleteDeadline() }
                        )
                    }
                    if (bookmark.expirationDate != null) {
                        TimerDisplay("Expiration", bookmark.expirationDate, Modifier.fillMaxWidth(),
                            onDelete = { model.deleteExpiration() }
                        )
                    }
                }
            }
        }
    }
}

private val SelectedBookmarkTypeModifier
    @Composable
    @ReadOnlyComposable
    get() = Modifier
        .styleProperty("pointer-events", "none")
        .backgroundColor(SilkTheme.palette.background)
        .color(MainStyle.primaryColor)
        .fontWeight(FontWeight.Lighter)