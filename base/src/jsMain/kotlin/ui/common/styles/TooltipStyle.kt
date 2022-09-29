package ui.common.styles

import org.jetbrains.compose.web.css.*

object TooltipStyle : StyleSheet() {

    val targetView by style {
        position(Position.Relative)
    }

    val popup by style {
        display(DisplayStyle.None)
        textAlign("center")
        borderRadius(6.px)
        position(Position.Absolute)
        property("z-index", 1)
    }

    val dirLeft by style {
        top((-5).px)
        right(110.percent)
        minWidth(60.px)
    }

    val dirRight by style {
        top((-5).px)
        left(110.percent)
        minWidth(60.px)
    }

    val dirTop by style {
        width(120.px)
        bottom(140.percent)
        left(50.percent)
        marginLeft((-60).px)
    }

    val dirBottom by style {
        width(120.px)
        top(140.percent)
        left(50.percent)
        marginLeft((-60).px)
    }

    enum class PopupDirection(val className: String) {
        TOP(dirTop),
        BOTTOM(dirBottom),
        LEFT(dirLeft),
        RIGHT(dirRight),
    }

    init {
        ".$targetView:hover .$popup" style {
            display(DisplayStyle.Block)
        }

        ".$targetView.${MainStyle.outline} .$popup" style {
            color(Color.white)
            border(0.px)
            padding(5.px)
            backgroundColor(MainStyle.primaryColor)
        }

        ".$targetView.${MainStyle.solid} .$popup" style {
            color(Color.black)
            border(1.px, LineStyle.Solid, MainStyle.primaryColor)
            padding(4.px)
            backgroundColor(Color.white)
        }

        ".$dirTop::after" style {
            property("content", "\" \"")
            display(DisplayStyle.Block)
            position(Position.Absolute)
            top(100.percent)
            left(50.percent)
            marginLeft((-5).px)
            border(5.px, LineStyle.Solid)
            property("border-color", "${MainStyle.primaryColor} transparent transparent transparent")
        }

        ".$dirBottom::after" style {
            property("content", "\" \"")
            display(DisplayStyle.Block)
            position(Position.Absolute)
            bottom(100.percent)
            left(50.percent)
            marginLeft((-5).px)
            border(5.px, LineStyle.Solid)
            property("border-color", "transparent transparent ${MainStyle.primaryColor} transparent")
        }

        ".$dirLeft::after" style {
            property("content", "\" \"")
            display(DisplayStyle.Block)
            position(Position.Absolute)
            top(50.percent)
            left(100.percent)
            marginTop((-5).px)
            border(5.px, LineStyle.Solid)
            property("border-color", "transparent transparent transparent ${MainStyle.primaryColor}")
        }

        ".$dirRight::after" style {
            property("content", "\" \"")
            display(DisplayStyle.Block)
            position(Position.Absolute)
            top(50.percent)
            right(100.percent)
            marginTop((-5).px)
            border(5.px, LineStyle.Solid)
            property("border-color", "transparent ${MainStyle.primaryColor} transparent transparent")
        }
    }
}