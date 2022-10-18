package ui.styles

import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.silk.components.forms.ButtonStyle
import common.styleProperty
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.value

object Variants {
    object Button {

        val SelectedUnclickablePrimary = ButtonStyle.addVariant("selected-unclickable-primary") {
            base {
                Modifier
                    .styleProperty("pointer-events", "none")
                    .backgroundColor(Color.transparent)
                    .color(Palette.Variable.color_primary.value())
                    .fontWeight(FontWeight.Lighter)
            }
        }

    }
}