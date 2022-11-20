package ui.page.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import com.varabyte.kobweb.compose.css.FontStyle
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributesBuilder
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.silk.components.icons.fa.*
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.components.text.SpanText
import common.styleProperty
import di.AppModule
import entity.BookmarkType
import entity.SingleBookmarkSource
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.TextArea
import ui.common.basecomponent.DivText
import ui.common.basecomponent.LoadableView
import ui.common.basecomponent.RowButton
import ui.common.basecomponent.TagListView
import ui.common.bookmark.BookmarkTitleEdit
import ui.common.bookmark.BookmarkTitleView
import ui.common.bookmark.BookmarkTypeBacklogButton
import ui.common.bookmark.BookmarkTypeLibraryButton
import ui.page.tagedit.TagEditView
import ui.styles.brand.DeadlineTimerIcon
import ui.styles.brand.ExpirationTimerIcon
import ui.styles.brand.ReminderTimerIcon
import ui.styles.components.BookmarkEditClickableArea

@Composable
fun BookmarkEditor(
    target: SingleBookmarkSource,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appModule = AppModule.Local.current
    val scope = rememberCoroutineScope()
    val onNavigateBackState = rememberUpdatedState(BookmarkEditorModel.OnNavigateBack(onNavigateBack))
    val model = remember(target) {
        appModule.createBookmarkEditorModel(scope, target, onNavigateBackState)
    }

    LoadableView(model.bookmark, modifier = modifier.minHeight(350.px)) { bookmark, m ->
        Column(Modifier.gap(8.px).padding(bottom = 8.px).then(m)) {
            Row(Modifier.fillMaxWidth().gap(8.px)) {

                RowButton(onClick = onNavigateBack) {
                    FaArrowLeft()
                    Text("Back")
                }

                Spacer()
                RowButton(
                    onClick = { model.updateFavorite(!bookmark.favorite) },
                ) {
                    FaStar(style = if (bookmark.favorite) IconStyle.FILLED else IconStyle.OUTLINE)
                    Text("Favorite")
                }
                if (!bookmark.isNew) {
                    RowButton(onClick = { model.deleteBookmark() }) {
                        FaTrash()
                        Text("Delete")
                    }
                }
            }

            if (model.editedBlock != BookmarkEditedBlock.TITLE) {
                BookmarkTitleView(bookmark.title, bookmark.base.favicon, bookmark.base.url,
                    BookmarkEditClickableArea.Style.toModifier()
                        .width(100.percent - 16.px)
                        .height(64.px)
                        .margin(leftRight = 4.px, topBottom = (-2).px) // Negative margin to compensate for border
                        .padding(leftRight = 2.px)
                        .onClick {
                            model.requestEdit(BookmarkEditedBlock.TITLE)
                        }
                )
            } else {
                BookmarkTitleEdit(
                    bookmark.title, bookmark.base.favicon,
                    onInput = { model.updateTitle(it) },
                    Modifier.margin(leftRight = 8.px)
                        .height(64.px)
                        .width(100.percent - 16.px)
                        .onKeyDown { event ->
                            if (event.getNormalizedKey() == "Enter") {
                                event.preventDefault()
                                model.requestEdit(null)
                            }
                        }
                )
            }

            Row(Modifier.fillMaxWidth().gap(8.px)) {
                val currentType = bookmark.currentType
                BookmarkTypeLibraryButton(currentType == BookmarkType.LIBRARY, Modifier.width(30.percent)) {
                    model.updateType(BookmarkType.LIBRARY)
                }
                BookmarkTypeBacklogButton(currentType == BookmarkType.BACKLOG, Modifier.width(30.percent)) {
                    model.updateType(BookmarkType.BACKLOG)
                }

                Spacer()

                RowButton(onClick = {
                    model.saveBookmark()
                }) {
                    FaFloppyDisk(style = IconStyle.FILLED)
                    Text("Save")
                }
            }

            Column(
                Modifier.padding(leftRight = 2.px)
                    .gap(8.px)
                    .width(100.percent - 4.px)
                    .margin(leftRight = (-4).px, topBottom = (-2).px)
                    .thenIf(model.editedBlock != BookmarkEditedBlock.COMMENT,
                        BookmarkEditClickableArea.Style.toModifier()
                            .onClick { model.requestEdit(BookmarkEditedBlock.COMMENT) }
                    )
                    .thenIf(model.editedBlock == BookmarkEditedBlock.COMMENT) {
                        Modifier.border(2.px, LineStyle.Solid, Color.transparent)
                    }
            ) {
                SpanText("Comment:")
                if (model.editedBlock != BookmarkEditedBlock.COMMENT) {
                    DivText(
                        bookmark.comment.takeIf { it.isNotBlank() } ?: "No comment",
                        Modifier.fontWeight(FontWeight.Lighter).margin(left = 8.px).width(100.percent - 8.px)
                            .thenIf(bookmark.comment.isBlank(), Modifier.fontStyle(FontStyle.Italic))
                    )
                } else {
                    TextArea(
                        bookmark.comment,
                        Modifier
                            .width(100.percent)
                            .lineHeight(1.2.em)
                            .minHeight(2.4.em)
                            .styleProperty("resize", "vertical")
                            .onKeyDown { event ->
                                if (event.getNormalizedKey() == "Enter") {
                                    event.preventDefault()
                                    model.requestEdit(null)
                                }
                            }.asAttributesBuilder {
                                onInput { model.updateComment(it.value) }
                            }
                    )
                    RowButton(
                        onClick = {
                            model.updateComment("")
                            model.requestEdit(null)
                        },
                        Modifier.align(Alignment.End).margin(right = (-2).px)
                    ) {
                        FaEraser()
                        Text("Clear")
                    }
                }
            }

            Column(Modifier.padding(left = 2.px, right = 2.px, bottom = 4.px)
                .gap(8.px)
                .width(100.percent - 4.px)
                .margin(left = (-4).px, right = (-4).px, top = (-2).px, bottom = (-6).px)
                .thenIf(model.editedBlock != BookmarkEditedBlock.TAGS,
                    BookmarkEditClickableArea.Style.toModifier()
                        .onClick { model.requestEdit(BookmarkEditedBlock.TAGS) }
                )
                .thenIf(model.editedBlock == BookmarkEditedBlock.TAGS) {
                    Modifier.border(2.px, LineStyle.Solid, Color.transparent)
                }) {

                SpanText("Tags:")

                when {
                    model.editedBlock == BookmarkEditedBlock.TAGS -> {
                        TagEditView(
                            bookmark.tags,
                            Modifier.margin(left = 8.px).width(100.percent - 8.px)
                        ) { event ->
                            model.onTagEvent(event)
                        }
                    }

                    bookmark.tags.isEmpty() -> {
                        SpanText(
                            "No tags",
                            Modifier.fontWeight(FontWeight.Lighter).margin(left = 8.px).width(100.percent - 8.px)
                                .fontStyle(FontStyle.Italic)
                        )
                    }

                    else -> {
                        TagListView(
                            bookmark.tags.toList(), Modifier.margin(leftRight = 8.px).width(100.percent - 16.px),
                        )
                    }
                }
            }

            SpanText("Timers:")

            @Composable
            fun TimerBlock(
                title: String,
                description: String,
                icon: @Composable () -> Unit,
                type: TimerType
            ) {
                TimerEditor(
                    title,
                    description,
                    icon,
                    model.editedBlock == type.block,
                    model.getTimerTarget(type),
                    Modifier
                        .width(100.percent - 6.px)
                        .margin(left = 6.px, top = (-2).px, bottom = (-2).px)
                        .thenIf(
                            model.editedBlock != type.block,
                            BookmarkEditClickableArea.Style.toModifier()
                                .onClick { model.requestEdit(type.block) }
                        )
                        .thenIf(model.editedBlock == type.block) {
                            Modifier.border(2.px, LineStyle.Solid, Color.transparent)
                        }
                ) { model.onTimerEvent(type, it) }
            }

            TimerBlock(
                "Reminder",
                "For tabs that you want to forget about until specific date",
                { ReminderTimerIcon() },
                TimerType.REMINDER
            )

            TimerBlock(
                "Deadline",
                "For tabs related to tasks that you need to finish until specific date",
                { DeadlineTimerIcon() },
                TimerType.DEADLINE
            )

            TimerBlock(
                "Expiration",
                "For tabs that you won't care after a specific date so they can be safely deleted",
                { ExpirationTimerIcon() },
                TimerType.EXPIRATION
            )
        }
    }
}