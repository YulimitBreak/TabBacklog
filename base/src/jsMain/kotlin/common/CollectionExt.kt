package common

fun <T> Iterable<T>.indicesOf(criteria: (T) -> Boolean) = mapIndexedNotNull { index: Int, t: T ->
    if (criteria(t)) index else null
}

fun <T, K> Iterable<T>.chunkedBy(keySelector: (T) -> K): List<List<T>> {
    if (this.isNullOrEmpty()) return emptyList()
    val result = mutableListOf<List<T>>()
    var tail = this.toList()
    while (tail.isNotEmpty()) {
        val key = keySelector(tail.first())
        val chunk = tail.takeWhile { keySelector(it) == key }
        result.add(chunk)
        tail = tail.drop(chunk.size)
    }
    return result.toList()
}

fun <T> List<T>.insertWithComparator(value: T, comparator: Comparator<T>): List<T> {
    val (head, tail) = partition { comparator.compare(it, value) < 0 }
    return head + value + tail
}