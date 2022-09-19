package ui.common

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLDivElement

object LoadingSpinner : StyleSheet() {
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
        property("border-top", "8px solid blue")
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
fun Loader(attrs: AttrBuilderContext<HTMLDivElement>? = null) {
    Style(LoadingSpinner)
    Div(
        attrs = {
            classes(LoadingSpinner.loader)
            attrs?.invoke(this)
        }
    )
}