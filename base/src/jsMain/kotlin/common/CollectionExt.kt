package common

fun <T> Iterable<T>.indicesOf(criteria: (T) -> Boolean) = mapIndexedNotNull { index: Int, t: T ->
    if (criteria(t)) index else null
}