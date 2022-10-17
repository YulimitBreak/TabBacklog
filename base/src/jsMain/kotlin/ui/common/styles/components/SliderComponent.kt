package ui.common.styles.components

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.hover
import com.varabyte.kobweb.silk.theme.shapes.Circle
import com.varabyte.kobweb.silk.theme.shapes.Rect
import com.varabyte.kobweb.silk.theme.shapes.clip
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.value
import ui.common.styles.Palette

object SliderComponent {

    val InactiveTrackStyle = ComponentStyle("slider-inactive-track") {
        base {
            Modifier.height(8.px).backgroundColor(Colors.LightGray).clip(Rect(4.px))
        }
    }

    val TrackStyle = ComponentStyle("slider-track") {
        base {
            Modifier.height(8.px).backgroundColor(Palette.Variable.color_primary.value()).clip(Rect(4.px))
        }
    }

    val ThumbStyle = ComponentStyle("slider-thumb") {
        base {
            Modifier.size(16.px).backgroundColor(Palette.Variable.color_primary.value()).clip(Circle())
        }

        hover {
            Modifier.backgroundColor(Palette.Variable.color_accent.value())
        }
    }
}