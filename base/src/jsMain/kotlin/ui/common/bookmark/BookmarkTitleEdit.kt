package ui.common.bookmark

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.TextArea
import org.w3c.dom.HTMLDivElement

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