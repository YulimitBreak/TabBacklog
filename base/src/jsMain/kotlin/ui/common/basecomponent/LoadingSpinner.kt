package ui.common.basecomponent

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributesBuilder
import com.varabyte.kobweb.compose.ui.modifiers.classNames
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import ui.common.styles.Palette

private object LoadingSpinnerStyleSheet : StyleSheet() {
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
        property("border-top", "8px solid ${Palette.Variable.color_primary.value()}")
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
    Style(LoadingSpinnerStyleSheet)
    Div(
        attrs = modifier.classNames(LoadingSpinnerStyleSheet.loader).asAttributesBuilder()
    )
}