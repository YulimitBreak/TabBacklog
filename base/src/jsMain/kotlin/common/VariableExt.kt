package common

import org.jetbrains.compose.web.css.*
import kotlin.properties.ReadOnlyProperty

fun StyleScope.variable(variable: CSSStyleVariable<StylePropertyNumber>, value: Number) {
    variable("--${variable.name}", value)
}

fun StyleScope.variable(variable: CSSStyleVariable<StylePropertyString>, value: String) {
    variable("--${variable.name}", value)
}

fun <TValue : StylePropertyValue> StyleScope.variable(variable: CSSStyleVariable<TValue>, value: TValue) {
    variable("--${variable.name}", value.toString())
}

fun <TValue : StylePropertyValue> variable(includePrefix: Boolean = true) =
    ReadOnlyProperty<Any?, CSSStyleVariable<TValue>> { parent, property ->
        val name = if (includePrefix && parent != null) {
            "${parent::class.simpleName}--${property.name}"
        } else {
            property.name
        }
        CSSStyleVariable(name)
    }