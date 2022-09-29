package ui.common.basecomponent

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLButtonElement
import ui.common.styles.MainStyle
import ui.common.styles.TooltipStyle
import ui.common.styles.UtilStyle

@Composable
fun IconButton(
    icon: String,
    title: String,
    popupDirection: TooltipStyle.PopupDirection,
    isSolid: Boolean = false,
    attrs: AttrBuilderContext<HTMLButtonElement>? = null,
    onClick: () -> Unit,
) {
    Div {
        Button(
            attrs = {
                classes(
                    UtilStyle.centerContent,
                    MainStyle.button,
                    if (isSolid) MainStyle.solid else MainStyle.outline,
                    TooltipStyle.targetView
                )
                attrs?.invoke(this)
                this.onClick { onClick() }
                style {
                    height(32.px)
                    width(32.px)
                }
            }
        ) {
            Img(src = icon,
                attrs = {
                    style {
                        width(24.px)
                        height(24.px)
                    }
                })
            Span(
                attrs = {
                    classes(
                        TooltipStyle.popup,
                        popupDirection.className,
                    )
                }
            ) {
                Text(title)
            }
        }
    }
}