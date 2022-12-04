package entity.core

import kotlinx.coroutines.*

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

/**
 * Loads data from [loader] and saves it into Loadable (assigned by [setter]) along with progress
 *
 * [debounceTime] when not null, pauses for this amount of time before showing loading - to avoid blinking
 * for short load
 */
fun <T> CoroutineScope.load(
    setter: (Loadable<T>) -> Unit,
    onSuccess: (T) -> Unit = {},
    debounceTime: Long = 0,
    loader: suspend () -> T
): Job = launch {
    var debounceJob: Job? = null
    if (debounceTime == 0L) {
        setter(Loadable.Loading())
    } else {
        debounceJob = launch {
            delay(debounceTime)
            setter(Loadable.Loading())
        }
    }
    try {
        val result = loader()
        debounceJob?.cancel()
        setter(Loadable.Success(result))
        onSuccess(result)
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        console.error(e)
        e.printStackTrace()
        debounceJob?.cancel()
        setter(Loadable.Error(e))
    }
}
