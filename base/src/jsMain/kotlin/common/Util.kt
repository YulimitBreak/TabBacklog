package common

fun Int.coerceIn(min: Int? = null, max: Int? = null): Int =
    when {
        min != null && this < min -> min
        max != null && this > max -> max
        else -> this
    }

