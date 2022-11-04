package entity.retrieve

interface RetrieveQuery<T> {
    interface Sort<T, R : Any> : RetrieveQuery<T> {
        val ascending: Boolean
        val from: R? get() = null
        val to: R? get() = null

        val fallbackSort: Comparator<T>? get() = null
    }

    interface Filter<T, R : Any> : RetrieveQuery<T> {
        val target: R
    }
}