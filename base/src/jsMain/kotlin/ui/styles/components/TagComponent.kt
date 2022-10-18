package ui.styles.components

import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.UserSelect
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.hover
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.value
import ui.styles.Palette

object TagComponent {
    val Style = ComponentStyle("text-tag") {
        base {
            Modifier.fontSize(0.8.em).padding(leftRight = 4.px, topBottom = 2.px)
                .backgroundColor(Palette.Variable.color_primary.value())
                .color(Palette.Variable.color_onPrimary.value())
                .userSelect(UserSelect.None)
                .borderRadius(4.px)
        }

    }

    val Clickable = Style.addVariant("clickable") {
        hover {
            Modifier.backgroundColor(Palette.Variable.color_primary_light.value())
                .cursor(Cursor.Pointer)
        }
    }

    val Selected = Style.addVariant("selected") {
        base {
            Modifier.backgroundColor(Palette.Variable.color_accent.value())
                .color(Palette.Variable.color_onAccent.value())
        }
    }
}