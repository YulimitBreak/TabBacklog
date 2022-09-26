package ui.common.styles

import org.jetbrains.compose.web.css.*

object UtilStyle : StyleSheet() {

    val centerContent by style {
        display(DisplayStyle.Flex)
        justifyContent(JustifyContent.Center)
        alignItems(AlignItems.Center)
    }

    val clickThrough by style {
        property("pointer-events", "none")
    }
}