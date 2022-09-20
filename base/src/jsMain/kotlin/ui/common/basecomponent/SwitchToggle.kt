package ui.common.basecomponent

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement

private class SwitchToggleStylesheet(val color: CSSColorValue) : StyleSheet() {

    val buttonLeft by style {

        padding(4.px)
        background("linear-gradient(to left, $color, $color 50%, #fff 52%, #fff")
        property("transition", "background-position 0.5s ease, color 0.5s ease")
        backgroundSize("200% 100%")
        textAlign("center")
        border(2.px, style = LineStyle.Solid, color = color)
        property("border-right", "1px $color")
    }

    val buttonRight by style {

        padding(4.px)
        background("linear-gradient(to right, $color, $color 50%, #fff 52%, #fff")
        property("transition", "background-position 0.5s ease, color 0.5s ease")
        backgroundSize("200% 100%")
        textAlign("center")
        border(2.px, style = LineStyle.Solid, color = color)
        property("border-left", "1px $color")
    }
}

@Composable
fun SwitchToggle(
    leftOption: String,
    rightOption: String,
    leftOptionSelected: Boolean,
    color: CSSColorValue,
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    onOptionSelect: (leftOptionSelected: Boolean) -> Unit
) {
    val stylesheet = SwitchToggleStylesheet(color)
    Style(stylesheet)
    Div(
        attrs = {
            attrs?.invoke(this)
            style {
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Row)
                alignItems(AlignItems.Center)
            }
        }
    ) {
        Div(attrs = {
            classes(stylesheet.buttonLeft)
            onClick { onOptionSelect(true) }
            style {
                width(50.percent)
                if (leftOptionSelected) {
                    cursor("default")
                    backgroundPosition("100% 0")
                    color(Color.white)
                } else {
                    cursor("pointer")
                    backgroundPosition("0% 0")
                    color(color)
                }
            }
        }

        ) {
            Text(leftOption)
        }
        Div(attrs = {
            classes(stylesheet.buttonRight)
            onClick { onOptionSelect(false) }
            style {
                width(50.percent)
                if (!leftOptionSelected) {
                    cursor("default")
                    backgroundPosition("0% 0")
                    color(Color.white)
                } else {
                    cursor("pointer")
                    backgroundPosition("100% 0")
                    color(color)
                }
            }
        }

        ) {
            Text(rightOption)
        }
    }
}