package ui.common

import androidx.compose.runtime.Composable
import entity.BookmarkType
import entity.EditedBookmark
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLDivElement
import ui.common.basecomponent.SwitchToggle

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

        BookmarkTypeSelector(bookmark.currentType, attrs = {
            style {
                width(80.percent)
                marginTop(16.px)
            }
        }, onTypeChanged = { onBookmarkChange(bookmark.copy(currentType = it)) })

        Div(attrs = {
            style {
                flex(1)
            }
        }) { }

        BookmarkCloseBar(
            attrs = {
                style {
                    width(100.percent)
                    margin(16.px)
                }
            },
            onCancel = {},
            onSave = {},
            onSaveAndClose = {},
        )
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
    type: BookmarkType,
    attrs: (AttrsScope<HTMLDivElement>.() -> Unit)? = null,
    onTypeChanged: (BookmarkType) -> Unit,
) {
    SwitchToggle(
        "Backlog",
        "Library",
        when (type) {
            BookmarkType.LIBRARY -> false
            BookmarkType.BACKLOG -> true
        },
        Color.crimson,
        attrs,
    ) {
        onTypeChanged(
            when (it) {
                true -> BookmarkType.BACKLOG
                false -> BookmarkType.LIBRARY
            }
        )
    }
}

@Composable
fun BookmarkCloseBar(
    attrs: (AttrsScope<HTMLDivElement>.() -> Unit)? = null,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    onSaveAndClose: () -> Unit,
) {
    Div(attrs = {
        attrs?.invoke(this)
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Row)
            justifyContent(JustifyContent.SpaceAround)
            alignItems(AlignItems.Center)
        }
    }
    ) {
        A(href = "javascript:void(0);", attrs = {
            style {
                color(Color.red)
            }
            onClick { onCancel() }
        }) {
            Text("Cancel")
        }
        A(href = "javascript:void(0);", attrs = {
            onClick { onSave() }
        }) {
            Text("Save")
        }
        A(href = "javascript:void(0);", attrs = {
            onClick { onSaveAndClose() }
        }) {
            Text("Save&Close")
        }
    }

}