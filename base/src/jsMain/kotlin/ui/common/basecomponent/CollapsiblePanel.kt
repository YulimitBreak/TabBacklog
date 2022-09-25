package ui.common.basecomponent

import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement

@Composable
fun CollapsiblePanel(
    title: String,
    expanded: Boolean,
    onExpand: (expanded: Boolean) -> Unit,
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    panelContent: @Composable ((expanded: Boolean) -> Unit)? = null,
    content: @Composable (() -> Unit)? = null,
) {

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
                    overflow("hidden")
                    if (expanded) {
                        property("border-bottom", "2px solid crimson")
                    }
                }
            }
        ) {
            Div(
                attrs = {
                    onClick { onExpand(!expanded) }
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
                Text(title)
            }
            if (panelContent != null) {
                Div(attrs = {
                    style {
                        gridRow("1/2")
                        gridColumn("2/3")
                        justifySelf("start")
                        alignSelf("center")
                        overflow("hidden")
                    }
                }) {
                    panelContent(expanded)
                }
            }
            Img(src = if (expanded) "chevron-up.svg" else "chevron-down.svg") {
                style {
                    width(40.px)
                    height(24.px)
                    gridRow("1/2")
                    paddingRight(8.px)
                    paddingLeft(8.px)
                    gridColumn("3/4")
                    justifySelf("end")
                    alignSelf("center")
                    property("pointer-events", "none")
                    background("linear-gradient(to right, #fff0, #fff 50%, #fff")
                }
            }
        }

        if (expanded) {
            content?.invoke()
        }
    }
}

@Composable
fun CollapsiblePanel(
    title: String,
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    panelContent: @Composable ((expanded: Boolean) -> Unit)? = null,
    content: @Composable (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }

    CollapsiblePanel(
        title,
        expanded,
        onExpand = { expanded = it },
        attrs = attrs,
        panelContent = panelContent,
        content = content
    )
}