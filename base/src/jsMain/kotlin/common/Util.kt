package common

fun Int.coerceIn(min: Int? = null, max: Int? = null): Int =
    when {
        min != null && this < min -> min
        max != null && this > max -> max
        else -> this
    }

fun generateHashcode(vararg values: Any?): Int {
    var hash = 7
    values.forEach {
        hash = 31 * hash + (it?.hashCode() ?: 0)
    }
    return hash
}