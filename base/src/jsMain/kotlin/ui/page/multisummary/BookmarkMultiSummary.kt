package ui.page.multisummary

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.icons.fa.FaPencil
import com.varabyte.kobweb.silk.components.icons.fa.FaStar
import com.varabyte.kobweb.silk.components.icons.fa.FaTrash
import com.varabyte.kobweb.silk.components.icons.fa.IconStyle
import com.varabyte.kobweb.silk.components.text.SpanText
import di.AppModule
import entity.BookmarkType
import entity.MultiBookmarkSource
import org.jetbrains.compose.web.css.minus
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Text
import ui.common.basecomponent.LoadableView
import ui.common.basecomponent.RowButton
import ui.common.basecomponent.TagListView
import ui.common.bookmark.BookmarkMultiTitleView
import ui.common.bookmark.BookmarkSummaryTimerView
import ui.common.bookmark.BookmarkTypeBacklogButton
import ui.common.bookmark.BookmarkTypeLibraryButton

@Composable
fun BookmarkMultiSummary(
    target: MultiBookmarkSource,
    onEditRequest: () -> Unit,
    firstButton: @Composable () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val appModule = AppModule.Local.current
    val scope = rememberCoroutineScope()

    val model: BookmarkMultiSummaryModel =
        remember(target) { appModule.createBookmarkMultiSummaryModel(scope, target) }

    LoadableView(model.bookmarks, modifier.minHeight(100.px)) { bookmarks, m ->
        Column(m.gap(8.px).margin(bottom = 8.px)) {
            Row(Modifier.fillMaxWidth().gap(8.px)) {

                firstButton()

                Spacer()
                if (bookmarks.anySaved) {
                    RowButton(
                        onClick = { model.updateAllFavorite(!bookmarks.allFavorite) },
                    ) {
                        FaStar(style = if (bookmarks.allFavorite) IconStyle.FILLED else IconStyle.OUTLINE)
                        Text("Favorite")
                    }
                    RowButton(onClick = { model.deleteAll() }) {
                        FaTrash()
                        Text("Delete")
                    }
                }
            }
            BookmarkMultiTitleView(
                bookmarks.titles,
                limit = 3,
                modifier = Modifier.margin(leftRight = 8.px).width(100.percent - 16.px).height(64.px)
            )
            Row(Modifier.fillMaxWidth().gap(8.px)) {

                BookmarkTypeLibraryButton(
                    bookmarks.types.singleOrNull() == BookmarkType.LIBRARY,
                    modifier = Modifier.width(30.percent)
                ) {
                    model.updateType(BookmarkType.LIBRARY)
                }
                BookmarkTypeBacklogButton(
                    bookmarks.types.singleOrNull() == BookmarkType.BACKLOG,
                    modifier = Modifier.width(30.percent)
                ) {
                    model.updateType(BookmarkType.BACKLOG)
                }

                Spacer()

                RowButton(onClick = {
                    onEditRequest()
                }) {
                    FaPencil()
                    Text("Edit")
                }
            }
            if (bookmarks.hasTags) {
                SpanText("Tags:")
                TagListView(
                    bookmarks.commonTags, Modifier.margin(leftRight = 8.px).width(100.percent - 16.px),
                    postfixTag = "${bookmarks.offTags.size} more"
                )
            }

            BookmarkSummaryTimerView(
                "Closest timers:",
                bookmarks.remindDate, bookmarks.deadline, bookmarks.expirationDate,
                onReminderDelete = { model.deleteReminder() },
                onDeadlineDelete = { model.deleteDeadline() },
                onExpirationDelete = { model.deleteExpiration() },
                modifier = Modifier.margin(left = 8.px).gap(8.px).width(100.percent - 8.px)
            )
        }
    }
}