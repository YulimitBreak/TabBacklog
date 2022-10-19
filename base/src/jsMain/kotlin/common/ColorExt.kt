package common

import com.varabyte.kobweb.compose.ui.graphics.Color

fun Color.alpha(alpha: Float): Color {
    val rgb = this.toRgb()
    return Color.rgba(rgb.red, rgb.green, rgb.blue, alpha)
}

fun Color.alpha(alpha: Int): Color {
    val rgb = this.toRgb()
    return Color.rgba(rgb.red, rgb.green, rgb.blue, alpha)
}