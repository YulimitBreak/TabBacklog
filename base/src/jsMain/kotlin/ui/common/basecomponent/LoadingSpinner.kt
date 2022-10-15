package ui.common.basecomponent

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import ui.common.styles.MainStyle

private class LoadingSpinnerStyleSheet(color: CSSColorValue) : StyleSheet() {
    private val keyframes by keyframes {
        from {
            property("transform", "rotate(0deg)")
        }

        to {
            property("transform", "rotate(360deg)")
        }
    }

    val loader by style {
        border(8.px, LineStyle.Solid, Color.lightgray)
        property("border-top", "8px solid $color")
        width(30.px)
        height(30.px)
        borderRadius(50.percent)
        animation(keyframes) {
            timingFunction(AnimationTimingFunction.Linear)
            duration(2.s)
            iterationCount(null)
        }
    }
}

@Composable
fun LoadingSpinner(modifier: Modifier = Modifier) {
    val stylesheet = LoadingSpinnerStyleSheet(MainStyle.primaryColor)
    Style(stylesheet)
    Div(
        attrs = { classes(stylesheet.loader) }
    )
}