package entity

sealed class Loadable<out T> {
    abstract val value: T?

    abstract fun <R> map(f: (T) -> R): Loadable<R>


    data class Success<T>(override val value: T) : Loadable<T>() {
        override fun <R> map(f: (T) -> R) = Success(f(value))
    }

    data class Error<T>(val error: Throwable) : Loadable<T>() {
        override val value = null

        override fun <R> map(f: (T) -> R) = Error<R>(error)
    }

    class Loading<T> : Loadable<T>() {
        override val value = null

        override fun equals(other: Any?): Boolean =
            other is Loading<*>

        override fun hashCode(): Int = 0

        override fun toString(): String =
            "Loading"

        override fun <R> map(f: (T) -> R) = Loading<R>()
    }
}