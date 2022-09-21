package ui.common.bookmark.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import entity.BookmarkType
import entity.EditedBookmark
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
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

        var timerState by rememberBookmarkTimerPanelState(
            bookmark.taskDeadline, bookmark.remindDate, bookmark.expirationDate,
        )

        BookmarkTimerPanel(timerState,
            attrs = {
                style {
                    width(100.percent)
                    marginTop(16.px)
                }
            },
            onStateChange = { state ->
                timerState = state
                onBookmarkChange(
                    bookmark.copy(
                        taskDeadline = state.deadline.toResultingDate(),
                        remindDate = state.reminder.toResultingDate(),
                        expirationDate = state.reminder.toResultingDate(),
                    )
                )
            })

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