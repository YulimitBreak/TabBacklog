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
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.icons.fa.*
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.components.text.SpanText
import di.AppModule
import entity.BookmarkType
import entity.MultiBookmarkSource
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Text
import ui.common.basecomponent.LoadableView
import ui.common.basecomponent.RowButton
import ui.common.basecomponent.TagListView
import ui.common.bookmark.*
import ui.common.datepicker.DatePickerMode
import ui.common.datepicker.DatePickerTarget
import ui.page.tagedit.TagEditView
import ui.styles.brand.DeadlineTimerIcon
import ui.styles.brand.ExpirationTimerIcon
import ui.styles.brand.ReminderTimerIcon
import ui.styles.components.BookmarkEditClickableArea

@Composable
fun BookmarkMultiEditor(
    target: MultiBookmarkSource,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appModule = AppModule.Local.current
    val scope = rememberCoroutineScope()
    val onNavigateBackState = rememberUpdatedState(BookmarkEditorModel.OnNavigateBack(onNavigateBack))
    val model = remember(target) {
        appModule.createBookmarkMultiEditorModel(scope, target, onNavigateBackState)
    }

    LoadableView(model.bookmarkBundle, modifier = modifier.minHeight(300.px)) { bookmarks, m ->
        Column(Modifier.gap(8.px).padding(bottom = 8.px).then(m)) {
            Row(Modifier.fillMaxWidth().gap(8.px)) {

                RowButton(onClick = onNavigateBack) {
                    FaArrowLeft()
                    Text("Back")
                }

                Spacer()
                RowButton(
                    onClick = { model.toggleFavorite() },
                ) {
                    if (bookmarks.favorite != null) {
                        FaStar(style = if (bookmarks.favorite) IconStyle.FILLED else IconStyle.OUTLINE)
                    } else {
                        FaStarHalfStroke()
                    }
                    Text("Favorite")
                }
                if (!bookmarks.isNew) {
                    RowButton(onClick = { model.deleteAll() }) {
                        FaTrash()
                        Text("Delete")
                    }
                }
            }

            BookmarkMultiTitleView(
                bookmarks.base.titles,
                limit = 3,
                modifier = Modifier.margin(leftRight = 8.px).width(100.percent - 16.px).height(64.px)
            )

            // TODO unselect type
            Row(Modifier.fillMaxWidth().gap(8.px)) {
                val currentType = bookmarks.currentType
                BookmarkTypeLibraryButton(currentType == BookmarkType.LIBRARY, Modifier.width(30.percent)) {
                    model.updateType(BookmarkType.LIBRARY)
                }
                BookmarkTypeBacklogButton(currentType == BookmarkType.BACKLOG, Modifier.width(30.percent)) {
                    model.updateType(BookmarkType.BACKLOG)
                }

                Spacer()

                RowButton(onClick = {
                    model.saveAll()
                }) {
                    FaFloppyDisk(style = IconStyle.FILLED)
                    Text("Save")
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
                            bookmarks.tags,
                            Modifier.margin(left = 8.px).width(100.percent - 8.px),
                            postfixTag = if (bookmarks.offTags.isNotEmpty()) "${bookmarks.offTags.size} more" else null
                        ) { event ->
                            model.onTagEvent(event)
                        }
                    }

                    bookmarks.tags.isEmpty() -> {
                        SpanText(
                            "No tags",
                            Modifier.fontWeight(FontWeight.Lighter).margin(left = 8.px).width(100.percent - 8.px)
                                .fontStyle(FontStyle.Italic)
                        )
                    }

                    else -> {
                        TagListView(
                            bookmarks.tags.toList(), Modifier.margin(leftRight = 8.px).width(100.percent - 16.px),
                            postfixTag = if (bookmarks.offTags.isNotEmpty()) "${bookmarks.offTags.size} more" else null
                        )
                    }
                }
            }

            SpanText("Timers:")
            @Composable
            fun TimerBlock(
                title: String,
                isUnset: Boolean,
                icon: @Composable () -> Unit,
                type: TimerType
            ) {
                TimerEditor(
                    title,
                    if (isUnset) "Different timers assigned" else "No timer assigned",
                    icon,
                    model.editedBlock == type.block,
                    if (isUnset) DatePickerTarget.None else model.getDatePickerTarget(type),
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
                        },
                    descriptionEnd = {
                        if (isUnset) {
                            Button(
                                onClick = {
                                    model.onTimerEvent(
                                        type,
                                        TimerEditorEvent.OnModeChange(DatePickerMode.NONE)
                                    )
                                    model.requestEdit(null)
                                },
                                Modifier.size(2.em)
                            ) {
                                FaTrash(Modifier.fontSize(1.em).padding(4.px))
                            }
                        }
                    }
                ) { model.onTimerEvent(type, it) }
            }

            TimerBlock(
                "Reminder",
                bookmarks.reminderUnset,
                { ReminderTimerIcon() },
                TimerType.REMINDER
            )

            TimerBlock(
                "Deadline",
                bookmarks.deadlineUnset,
                { DeadlineTimerIcon() },
                TimerType.DEADLINE
            )

            TimerBlock(
                "Expiration",
                bookmarks.expirationUnset,
                { ExpirationTimerIcon() },
                TimerType.EXPIRATION
            )
        }
    }
}