package ui.styles.components

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.graphics.lightened
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.silk.components.style.ComponentStyle

object TableContentComponent {
    val Style = ComponentStyle("table-content") {

        cssRule("> *:nth-child(odd)") {
            Modifier.backgroundColor(Colors.White)
        }

        cssRule("> *:nth-child(even)") {
            Modifier.backgroundColor(Colors.Navy.lightened(0.9f))
        }
    }
}