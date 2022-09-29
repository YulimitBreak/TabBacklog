package ui.common.basecomponent

import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLButtonElement
import ui.common.styles.MainStyle

@Composable
fun IconButton(
    icon: String,
    title: String,
    isSolid: Boolean = false,
    attrs: AttrBuilderContext<HTMLButtonElement>? = null,
    onClick: () -> Unit,
) {

    var isHovered by remember { mutableStateOf(false) }

    Button(
        attrs = {
            classes(
                MainStyle.button,
                if (isSolid) MainStyle.solid else MainStyle.outline
            )
            attrs?.invoke(this)
            this.onClick { onClick() }
            this.onMouseEnter { isHovered = true }
            this.onMouseLeave { isHovered = false }
            style {
                height(24.px)
                padding(4.px)
                if (isHovered) {
                    backgroundColor(Color.blue)
                }
            }
        }
    ) {
        Img(src = icon,
            attrs = {
                style {
                    width(16.px)
                    height(16.px)
                }
            })
        Text(title)
    }
}