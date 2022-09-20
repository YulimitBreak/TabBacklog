package ui.common

import androidx.compose.runtime.Composable
import entity.BookmarkType
import entity.EditedBookmark
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.max
import org.jetbrains.compose.web.attributes.min
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLDivElement

@Composable
fun BookmarkEditor(
    bookmark: EditedBookmark,
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    onBookmarkChange: (EditedBookmark) -> Unit
) {

    Div(attrs = {
        attrs?.invoke(this)
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
            alignItems(AlignItems.Center)
            justifyContent(JustifyContent.Stretch)
            marginTop(16.px)
            marginBottom(16.px)
        }
    }) {
        BookmarkTitleEdit(bookmark.title, bookmark.base.favicon, bookmark.base.url, attrs = {
            style { width(100.percent) }
        }, onTitleChanged = { onBookmarkChange(bookmark.copy(title = it)) })
        BookmarkTypeSelector(bookmark.isNew, bookmark.currentType, attrs = {
            style {
                width(100.percent)
                marginTop(8.px)
            }
        }, onChange = { onBookmarkChange(bookmark.copy(currentType = it)) })

        Div(attrs = {
            style {
                flex(1)
            }
        }) { }

        Text(bookmark.currentType.toString())
    }
}

@Composable
fun BookmarkTitleEdit(
    title: String,
    favicon: String?,
    url: String,
    attrs: (AttrsScope<HTMLDivElement>.() -> Unit)? = null,
    onTitleChanged: (String) -> Unit
) {
    Div(attrs = {
        attrs?.invoke(this)
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Row)
            alignItems(AlignItems.Center)
            justifyContent(JustifyContent.Start)
        }
    }) {
        if (favicon != null) {
            Img(src = favicon, attrs = {
                style {
                    width(32.px)
                    height(32.px)
                    marginRight(16.px)
                }
            })
        }

        Div(attrs = {
            style {
                flexShrink(1)
                width(100.percent)
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Column)
                justifyContent(JustifyContent.Center)
                alignItems(AlignItems.Start)
            }
        }) {

            TextArea {
                title(title)
                value(title)
                onInput { onTitleChanged(it.value) }
                style {
                    property("resize", "none")
                    border(1.px)
                    width(100.percent)
                }
            }
            Div(attrs = {
                title(url)
                style {
                    property("text-overflow", "ellipsis")
                    property("overflow-wrap", "anywhere")
                    whiteSpace("nowrap")
                    overflow("hidden")
                    color(Color.gray)
                    fontSize(10.px)
                    width(200.px)
                }
            }) {
                Text(url)
            }
        }
    }
}

@Composable
fun BookmarkTypeSelector(
    isNew: Boolean,
    current: BookmarkType?,
    attrs: (AttrsScope<HTMLDivElement>.() -> Unit)? = null,
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