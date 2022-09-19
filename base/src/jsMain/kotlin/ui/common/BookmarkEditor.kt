package ui.common

import androidx.compose.runtime.Composable
import entity.BookmarkType
import entity.EditedBookmark
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.max
import org.jetbrains.compose.web.attributes.min
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement

@Composable
fun BookmarkEditor(bookmark: EditedBookmark, onBookmarkChange: (EditedBookmark) -> Unit) {

    Div(
        attrs = {
            style {
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Column)
                alignItems(AlignItems.Center)
            }
        }
    ) {
        BookmarkTypeSelector(
            bookmark.isNew, bookmark.currentType, attrs = {
                style { width(100.percent) }
            }, onChange = { onBookmarkChange(bookmark.copy(currentType = it)) }
        )

        Text(bookmark.currentType.toString())
    }
}

@Composable
fun BookmarkTypeSelector(
    isNew: Boolean,
    current: BookmarkType?,
    attrs: (AttrsScope<HTMLDivElement>.() -> Unit)?,
    onChange: (BookmarkType?) -> Unit
) {
    Div(attrs = {
        attrs?.invoke(this)
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
            alignItems(AlignItems.Center)
        }
    }) {
        Input(InputType.Range) {
            style {
                width(90.percent)
            }
            min("0")
            max(if (isNew) "3" else "4")
            value(
                when (current) {
                    BookmarkType.LIBRARY -> 0
                    BookmarkType.BACKLOG -> 1
                    BookmarkType.TASK -> 2
                    BookmarkType.REMINDER -> 3
                    null -> 4
                }
            )
            onInput {
                onChange(
                    when (it.value) {
                        0 -> BookmarkType.LIBRARY
                        1 -> BookmarkType.BACKLOG
                        2 -> BookmarkType.TASK
                        3 -> BookmarkType.REMINDER
                        else -> null
                    }
                )
            }
        }
        Div(attrs = {
            style {
                marginTop(0.px)
                marginBottom(0.px)
                width(100.percent)
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Row)
                alignItems(AlignItems.Start)
                justifyContent(JustifyContent.SpaceBetween)
            }
        }) {
            Div { Text("Library") }
            Div { Text("Backlog") }
            Div { Text("Task") }
            Div { Text("Reminder") }
            if (!isNew) {
                Div(attrs = {
                    style { color(Color.red) }
                }) { Text("Delete") }
            }
        }
    }
}