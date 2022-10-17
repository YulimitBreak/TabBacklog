package ui.common.basecomponent

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributesBuilder
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.style.toModifier
import org.jetbrains.compose.web.css.minus
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div
import ui.common.styles.components.SliderComponent

@Composable
fun Slider(
    value: Int,
    maxValue: Int,
    modifier: Modifier = Modifier,
    onValueChange: (Int) -> Unit
) {

    val percent = value * 100f / maxValue.coerceAtLeast(1)
    Box(contentAlignment = Alignment.Center, modifier = modifier.role("slider")) {
        // Nesting containers to avoid a problem with paddings
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxHeight().width(100.percent - 16.px).margin(leftRight = 8.px)
        ) {
            Div(
                attrs = SliderComponent.TrackStyle.toModifier()
                    .width(percent.percent)
                    .align(Alignment.CenterStart)
                    .asAttributesBuilder()
            )

            Div(
                attrs = SliderComponent.InactiveTrackStyle.toModifier()
                    .width((100 - percent).percent)
                    .align(Alignment.CenterEnd)
                    .asAttributesBuilder()
            )

            Div(
                attrs = SliderComponent.ThumbStyle.toModifier()
                    .tabIndex(0)
                    .align(Alignment.CenterStart)
                    .margin(left = percent.percent - 8.px)
                    .asAttributesBuilder()
            )
        }
    }
}