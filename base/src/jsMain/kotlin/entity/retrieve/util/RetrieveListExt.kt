package entity.retrieve.util

import entity.retrieve.RetrieveQuery

fun <T, R> Collection<T>.selectByField(
    field: (T) -> R?,
    comparator: Comparator<R>,
    ascending: Boolean,
    from: R?,
    to: R?,
    fallbackSort: Comparator<T>?,
): List<T> {
    var result = filter { field(it) != null }
        .sortedWith { a, b ->
            val compareValues =
                (if (ascending) comparator else comparator.reversed()).compare(field(a)!!, field(b)!!)
            if (compareValues == 0) fallbackSort?.compare(a, b) ?: compareValues else compareValues
        }
    if (from != null) {
        result = result.dropWhile { comparator.compare(field(it)!!, from) < 0 }
    }
    if (to != null) {
        result = result.dropLastWhile { comparator.compare(field(it)!!, to) > 0 }
    }
    return result
}

fun <T, R : Comparable<R>> Collection<T>.selectByField(
    field: (T) -> R?,
    query: RetrieveQuery.Sort<T, R>
) = selectByField(field, naturalOrder(), query.ascending, query.from, query.to, query.fallbackSort)

fun <T, R : Any> Collection<T>.selectByField(
    field: (T) -> R?,
    comparator: Comparator<R>,
    query: RetrieveQuery.Sort<T, R>
) = selectByField(field, comparator, query.ascending, query.from, query.to, query.fallbackSort)

