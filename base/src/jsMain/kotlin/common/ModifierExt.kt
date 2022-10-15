package common

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.StylePropertyValue

fun Modifier.styleProperty(propertyName: String, value: String) = styleModifier {
    property(propertyName, value)
}

fun Modifier.styleProperty(propertyName: String, value: Number) = styleModifier {
    property(propertyName, value)
}

fun Modifier.styleProperty(propertyName: String, value: StylePropertyValue) = styleModifier {
    property(propertyName, value)
}