package common

interface CombinableComparator<T> : Comparator<T> {

    val next: Comparator<T>? get() = null

    val isReversed: Boolean get() = false

    fun isLess(first: T, second: T): Boolean

    fun isEqual(first: T, second: T): Boolean

    operator fun T.compareTo(other: T): Int = compare(this, other)

    override fun compare(a: T, b: T): Int =
        when {
            isLess(a, b) -> -1
            isEqual(a, b) -> next?.compare(a, b) ?: 0
            else -> 1
        }.let {
            if (isReversed) -it else it
        }
}