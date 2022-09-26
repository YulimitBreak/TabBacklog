package ui.common.basecomponent

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import org.jetbrains.compose.web.attributes.list
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLDivElement
import ui.common.styles.UtilStyle

@Composable
fun TagInput(
    currentInput: String,
    confirmedTags: List<String>,
    suggestedTags: List<String>,
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    onTagInput: (String) -> Unit,
    onTagConfirm: (String) -> Unit,
    onConfirmedTagEdited: (String) -> Unit,
    onConfirmedTagDeleted: (String) -> Unit,
) {

    Div(attrs = {
        attrs?.invoke(this)
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
            justifyContent(JustifyContent.Center)
            alignItems(AlignItems.Stretch)
        }
    }) {

        Datalist(attrs = {
            id(SUGGESTION_DATA_LIST_ID)
        }) {

            suggestedTags.forEach {
                key(it) {
                    Option(it)
                }
            }
        }

        TextInput {
            value(currentInput)
            list(SUGGESTION_DATA_LIST_ID)
            var userInput = false
            onKeyDown {
                userInput = it.key != undefined
                if (it.getNormalizedKey() == "Enter") {
                    onTagConfirm(currentInput)
                }
            }
            onInput {
                onTagInput(it.value)
                console.log(userInput.toString())
                if (!userInput) {
                    onTagConfirm(it.value)
                }
            }
        }

        Div(attrs = {
            style {
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Row)
                flexWrap(FlexWrap.Wrap)
                alignContent(AlignContent.Center)
                marginTop(8.px)
                gap(4.px)
            }
        }) {
            confirmedTags.forEach { tag ->
                key(tag) {
                    ConfirmedTagLabel(
                        tag,
                        attrs = {
                            style {
                                height(16.px)
                                fontSize(10.px)
                            }
                        },
                        onTagEdited = {
                            onConfirmedTagEdited(tag)
                        },
                        onTagDeleted = {
                            onConfirmedTagDeleted(tag)
                        }
                    )
                }
            }
        }
    }
}

private const val SUGGESTION_DATA_LIST_ID = "suggestions"

@Composable
fun ConfirmedTagLabel(
    text: String,
    attrs: AttrBuilderContext<HTMLDivElement>?,
    onTagEdited: () -> Unit,
    onTagDeleted: () -> Unit,
) {
    Div(attrs = {
        attrs?.invoke(this)
        style {
            border(0.px)
            color(Color.white)
            backgroundColor(Color.crimson)
            borderRadius(4.px)
            display(DisplayStyle.Flex)
            alignItems(AlignItems.Center)
            flexDirection(FlexDirection.Row)
        }
    }) {

        Div(attrs = {
            classes(UtilStyle.centerContent)
            onClick { onTagDeleted() }
            style {
                height(100.percent)
                padding(4.px)
                cursor("pointer")
            }
        }) {
            Text("x")
        }

        Div(attrs = {
            onClick { onTagEdited() }
            style {
                height(100.percent)
                padding(4.px)
                display(DisplayStyle.Flex)
                justifyContent(JustifyContent.Center)
                alignItems(AlignItems.Center)
                cursor("pointer")
            }
        }) {
            Text(text)
        }
    }
}