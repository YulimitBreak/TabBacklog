package ui.common.bookmark.editor

import androidx.compose.runtime.*
import entity.BookmarkType
import entity.EditedBookmark
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

        var timerState by rememberBookmarkTimerPanelState(
            bookmark.taskDeadline, bookmark.remindDate, bookmark.expirationDate,
        )

        BookmarkTimerPanelContainer(
            timerState,
            attrs = {
                style {
                    width(90.percent)
                    marginTop(16.px)
                }
            },
            onStateChanged = { state ->
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
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
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
fun BookmarkTimerPanelContainer(
    state: BookmarkTimerPanelState,
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    onStateChanged: (BookmarkTimerPanelState) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Div(attrs = {
        attrs?.invoke(this)
        style {
            marginLeft(16.px)
            marginRight(16.px)
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
            justifyContent(JustifyContent.Start)
            alignItems(AlignItems.Center)
            border(2.px, style = LineStyle.Solid, color = Color.crimson)
        }
    }) {
        Div(
            attrs = {

                style {
                    width(100.percent)
                    height(24.px)
                    display(DisplayStyle.Grid)
                    gridTemplateRows("1f")
                    gridTemplateColumns("25% 50% 25%")
                    if (expanded) {
                        property("border-bottom", "2px solid crimson")
                    }
                }
            }
        ) {
            Div(
                attrs = {
                    onClick { expanded = !expanded }
                    style {
                        gridRow("1/2")
                        gridColumn("1/4")
                        cursor("pointer")
                    }
                }
            )
            Div(
                attrs = {
                    style {
                        gridRow("1/2")
                        gridColumn("1/2")
                        property("pointer-events", "none")
                        display(DisplayStyle.Flex)
                        justifyContent(JustifyContent.Center)
                        alignItems(AlignItems.Center)
                    }
                }
            ) {
                Text("Timers")
            }
            if (state.hasTimers) {
                Button(attrs = {
                    onClick {
                        onStateChanged(BookmarkTimerPanelState.fromInitialDate(null, null, null))
                    }
                    style {
                        gridRow("1/2")
                        gridColumn("2/3")
                        justifySelf("start")
                        alignSelf("center")
                        height(24.px)
                        padding(4.px)
                        paddingLeft(8.px)
                        paddingRight(8.px)
                        color(Color.white)
                        backgroundColor(Color.crimson)
                        border(0.px)
                        cursor("pointer")
                    }
                }) {
                    Text("Clear")
                }
            }
            Img(src = if (expanded) "chevron-down.svg" else "chevron-up.svg") {
                style {
                    width(24.px)
                    height(24.px)
                    gridRow("1/2")
                    marginRight(8.px)
                    gridColumn("3/4")
                    justifySelf("end")
                    alignSelf("center")
                    property("pointer-events", "none")
                }
            }
        }

        if (expanded) {
            BookmarkTimerPanel(
                state,
                attrs = {
                    style {
                        width(100.percent)
                        marginTop(16.px)
                        marginBottom(16.px)
                    }
                },
                onStateChanged = onStateChanged
            )
        }
    }
}

@Composable
fun BookmarkCloseBar(
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
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