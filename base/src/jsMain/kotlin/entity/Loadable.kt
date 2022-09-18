package entity

sealed class Loadable<out T> {
    abstract val value: T?

    data class Success<T>(override val value: T) : Loadable<T>()

    data class Error<T>(val error: Throwable) : Loadable<T>() {
        override val value = null
    }

    class Loading<T> : Loadable<T>() {
        override val value = null

        override fun equals(other: Any?): Boolean =
            other is Loading<*>

        override fun hashCode(): Int = 0

        override fun toString(): String =
            "Loading"
    }
}