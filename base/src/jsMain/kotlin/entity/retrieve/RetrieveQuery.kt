package entity.retrieve

interface RetrieveQuery<T> {
    interface Sort<T, R : Any> : RetrieveQuery<T> {
        @Suppress("UNCHECKED_CAST")
        val comparator
            get() = Comparator<R> { a, b ->
                (a as Comparable<R>).compareTo(b)
            }

        val ascending: Boolean
        val from: R? get() = null
        val to: R? get() = null
    }

    interface Filter<T, R : Any> : RetrieveQuery<T> {
        val target: R
    }
}