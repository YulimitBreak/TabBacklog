package ui.page.editor

import androidx.compose.runtime.*
import di.ModuleLocal
import entity.Bookmark
import entity.BookmarkType
import entity.EditedBookmark
import org.jetbrains.compose.web.attributes.InputMode
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLDivElement
import ui.common.basecomponent.CollapsiblePanel
import ui.common.basecomponent.SwitchToggle
import ui.common.basecomponent.TagInput
import ui.common.bookmark.*
import ui.common.styles.MainStyle
import ui.common.styles.TooltipStyle
import ui.common.styles.UtilStyle

@Composable
fun OldBookmarkEditor(
    baseBookmark: Bookmark,
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
) {

    val appModule = ModuleLocal.App.current
    val scope = rememberCoroutineScope()
    val model: OldBookmarkEditorModel = remember(baseBookmark) {
        OldBookmarkEditorModel(baseBookmark, scope, TODO(), TODO())
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
        OldBookmarkTitleEdit(bookmark.title, bookmark.base.favicon, bookmark.base.url, attrs = {
            style { width(100.percent) }
        }, onTitleChanged = { model.onTitleChanged(it) })

        BookmarkTypeSelector(bookmark.currentType, attrs = {
            style {
                width(80.percent)
                marginTop(16.px)
            }
        }, onTypeChanged = { model.onTypeChanged(it) })

        FavoriteButton(
            bookmark.favorite,
            TooltipStyle.PopupDirection.BOTTOM,
            onChange = { model.onFavoriteChanged(it) },
            attrs = {
                style { width(50.percent) }
            }
        )

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
            classes(MainStyle.outline)
            inputMode(InputMode.Text)
            value(bookmark.comment)
            onInput { model.onCommentChanged(it.value) }
            style {
                property("resize", "none")
                width(90.percent)
                marginTop(8.px)
            }
        }

        CollapsiblePanel(
            title = "Tags",
            attrs = {
                style {
                    width(90.percent)
                    marginTop(16.px)
                }
            },
            expanded = model.state.openedPanel == OldBookmarkEditorModel.OpenedPanel.TAGS,
            onExpand = { model.toggleOpenedPanel(OldBookmarkEditorModel.OpenedPanel.TAGS) },
            panelContent = { expanded ->
                if (!expanded) {
                    Div(
                        attrs = {
                            style {
                                display(DisplayStyle.Flex)
                                width(100.percent)
                                height(100.percent)
                                justifyContent(JustifyContent.Start)
                                alignItems(AlignItems.Center)
                                flexDirection(FlexDirection.Row)
                                columnGap(5.px)
                            }
                        }
                    ) {
                        bookmark.tags.take(5).forEach { tag ->
                            key(tag) {
                                Div(attrs = {
                                    onClick {
                                        // Because pointer-events: none doesn't work for some reason
                                        model.toggleOpenedPanel(OldBookmarkEditorModel.OpenedPanel.TAGS)
                                    }
                                    classes(MainStyle.tag, UtilStyle.centerContent)
                                    style {
                                        cursor("pointer")
                                    }
                                }) {
                                    Text(tag)
                                }
                            }
                        }
                    }
                }
            }
        ) {
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
                        marginBottom(16.px)
                    }
                }

            )
        }

        var timerState by rememberBookmarkTimerPanelState(
            bookmark.deadline, bookmark.reminder, bookmark.expiration,
        )

        fun onTimerStateChanged(state: BookmarkTimerPanelState) {
            timerState = state
            model.onTimersChanged(
                deadline = state.deadline.toResultingDate(),
                reminder = state.reminder.toResultingDate(),
                expiration = state.expiration.toResultingDate(),
            )
        }

        CollapsiblePanel(
            title = "Timers",
            attrs = {
                style {
                    width(90.percent)
                    marginTop(16.px)
                }
            },
            expanded = model.state.openedPanel == OldBookmarkEditorModel.OpenedPanel.TIMERS,
            onExpand = { model.toggleOpenedPanel(OldBookmarkEditorModel.OpenedPanel.TIMERS) },
            panelContent = {
                if (timerState.hasTimers) {
                    Button(attrs = {
                        onClick {
                            onTimerStateChanged(
                                BookmarkTimerPanelState.fromInitialDate(null, null, null)
                            )
                        }
                        classes(MainStyle.button, MainStyle.solid)
                        style {
                            height(24.px)
                            padding(4.px)
                            paddingLeft(8.px)
                            paddingRight(8.px)
                        }
                    }) {
                        Text("Clear")
                    }
                }
            }) {
            BookmarkTimerPanel(
                timerState,
                attrs = {
                    style {
                        width(100.percent)
                        marginTop(16.px)
                        marginBottom(16.px)
                    }
                },
                onStateChanged = { onTimerStateChanged(it) }
            )
        }

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
        MainStyle.primaryColor,
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