package ui.common.styles

import org.jetbrains.compose.web.css.*

open class MainStyleBase(val primaryColor: CSSColorValue) : StyleSheet() {

    val solid by style {
        border(1.px, LineStyle.Solid, primaryColor)
        backgroundColor(primaryColor)
        color(Color.white)
    }

    val outline by style {
        border(1.px, LineStyle.Solid, primaryColor)
        backgroundColor(Color.white)
    }

    val button by style {
        cursor("pointer")
        self + outline style {
            color(primaryColor)
            border(2.px, LineStyle.Solid, primaryColor)
        }
    }

    val tag by style {
        border(0.px)
        backgroundColor(primaryColor)
        color(Color.white)
        fontSize(0.8.em)
        borderRadius(4.px)
        height(1.2.em)
        paddingLeft(4.px)
        paddingRight(4.px)
    }
}

object MainStyle : MainStyleBase(Color.darkblue)