package ui.common.bookmark.editor

import androidx.compose.runtime.*
import di.ModuleLocal
import entity.Bookmark
import entity.BookmarkType
import entity.EditedBookmark
import org.jetbrains.compose.web.attributes.InputMode
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLDivElement
import ui.common.basecomponent.SwitchToggle
import ui.common.basecomponent.TagInput

@Composable
fun BookmarkEditor(
    baseBookmark: Bookmark,
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
) {

    val appModule = ModuleLocal.App.current
    val scope = rememberCoroutineScope()
    val model = remember(baseBookmark) {
        appModule.createBookmarkEditorModel(scope, baseBookmark)
    }

    val bookmark: EditedBookmark = model.state.bookmark

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
        }, onTitleChanged = { model.onTitleChanged(it) })

        BookmarkTypeSelector(bookmark.currentType, attrs = {
            style {
                width(80.percent)
                marginTop(16.px)
            }
        }, onTypeChanged = { model.onTypeChanged(it) })

        Div(attrs = {
            title("A reminder, why this page is in backlog or library")
            style {
                alignSelf(AlignSelf.SelfStart)
                marginLeft(16.px)
                marginTop(16.px)
            }
        }
        ) {
            Text("Comment")
        }

        TextArea {
            inputMode(InputMode.Text)
            value(bookmark.comment)
            onInput { model.onCommentChanged(it.value) }
            style {
                border(2.px, LineStyle.Solid, Color.crimson)
                property("resize", "none")
                width(90.percent)
                marginTop(8.px)
            }
        }


        TagInput(
            model.state.tagInputUiState.currentInput,
            bookmark.tags,
            model.state.tagInputUiState.suggestedTags,
            onTagInput = { model.updateTagInput(it) },
            onTagConfirm = { model.onTagConfirm(it) },
            onConfirmedTagEdited = { model.onConfirmedTagEdited(it) },
            onConfirmedTagDeleted = { model.onConfirmedTagDeleted(it) },
            attrs = {
                style {
                    width(90.percent)
                    marginTop(16.px)
                }
            }

        )

        var timerState by rememberBookmarkTimerPanelState(
            bookmark.deadline, bookmark.reminder, bookmark.expiration,
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
                model.onTimersChanged(
                    deadline = state.deadline.toResultingDate(),
                    reminder = state.reminder.toResultingDate(),
                    expiration = state.reminder.toResultingDate(),
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