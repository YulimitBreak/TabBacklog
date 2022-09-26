package ui.common.basecomponent

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement

@Composable
fun FavoriteButton(
    isFavorite: Boolean,
    onChange: (isFavorite: Boolean) -> Unit,
    attrs: AttrBuilderContext<HTMLDivElement>? = null
) {
    Div(
        attrs = {
            attrs?.invoke(this)
            onClick { onChange(!isFavorite) }
            style {
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Row)
                justifyContent(JustifyContent.Center)
                alignItems(AlignItems.Center)
                border(2.px, LineStyle.Solid, Color.crimson)
                padding(4.px)
                height(16.px)
                cursor("pointer")
            }
        }
    ) {
        Img(if (isFavorite) "star.svg" else "star-outline.svg", attrs = {
            style {
                width(24.px)
                height(24.px)
            }
        })
        Div(attrs = {
            style { color(Color.crimson) }
        }) {
            Text("Favorite")
        }
    }
}