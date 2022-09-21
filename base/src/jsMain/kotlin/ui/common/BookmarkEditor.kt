package ui.common

import androidx.compose.runtime.*
import entity.BookmarkType
import entity.EditedBookmark
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLDivElement
import ui.common.basecomponent.RelativeDatePicker
import ui.common.basecomponent.RelativeDatePickerState
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
fun BookmarkTimerPanel(
    state: BookmarkTimerPanelState,
    attrs: (AttrsScope<HTMLDivElement>.() -> Unit)? = null,
    onStateChange: (BookmarkTimerPanelState) -> Unit,
) {

    Div(
        attrs = {
            attrs?.invoke(this)
            style {
                display(DisplayStyle.Grid)
                gridTemplateColumns("2fr 3fr")
                gridTemplateRows("1fr 1fr 1fr")
            }
        }
    ) {
        Div(
            attrs = {
                title("For links that represent tasks that need to be done by specific date")
                style {
                    gridRow("1/2")
                    gridColumn("1/2")
                    display(DisplayStyle.Flex)
                    justifyContent(JustifyContent.Center)
                    alignItems(AlignItems.Center)
                }
            }
        ) {
            Text("Deadline")
        }
        RelativeDatePicker(
            state.deadline,
            attrs = {
                style {
                    gridRow("1/2")
                    gridColumn("2/3")
                }
            },
            onStateUpdate = { onStateChange(state.copy(deadline = it)) }
        )
        Div(
            attrs = {
                title("For links that will be hidden in the list until reminder date")
                style {
                    gridRow("2/3")
                    gridColumn("1/2")
                    display(DisplayStyle.Flex)
                    justifyContent(JustifyContent.Center)
                    alignItems(AlignItems.Center)
                }
            }
        ) {
            Text("Reminder")
        }
        RelativeDatePicker(
            state.reminder,
            attrs = {
                style {
                    gridRow("2/3")
                    gridColumn("2/3")
                }
            },
            onStateUpdate = { onStateChange(state.copy(reminder = it)) }
        )
        Div(
            attrs = {
                title("For links that should be deleted after specific date")
                style {
                    gridRow("3/4")
                    gridColumn("1/2")
                    display(DisplayStyle.Flex)
                    justifyContent(JustifyContent.Center)
                    alignItems(AlignItems.Center)
                }
            }
        ) {
            Text("Expiration")
        }
        RelativeDatePicker(
            state.expiration,
            attrs = {
                style {
                    gridRow("3/4")
                    gridColumn("2/3")
                }
            },
            onStateUpdate = { onStateChange(state.copy(expiration = it)) }
        )
    }
}

@Composable
fun rememberBookmarkTimerPanelState(
    initialDeadline: LocalDate?,
    initialReminder: LocalDate?,
    initialExpiration: LocalDate?
) = remember {
    mutableStateOf(
        BookmarkTimerPanelState(
            deadline = RelativeDatePickerState.fromInitialDate(initialDeadline),
            reminder = RelativeDatePickerState.fromInitialDate(initialReminder),
            expiration = RelativeDatePickerState.fromInitialDate(initialExpiration)
        )
    )
}

data class BookmarkTimerPanelState(
    val deadline: RelativeDatePickerState,
    val reminder: RelativeDatePickerState,
    val expiration: RelativeDatePickerState,
)

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