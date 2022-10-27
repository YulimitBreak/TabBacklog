package common

interface CombinableFilter<T> {

    val parent: CombinableFilter<T>? get() = null

    fun matchesFilter(item: T): Boolean

    fun filter(item: T) = matchesFilter(item) || parent?.matchesFilter(item) ?: true
}