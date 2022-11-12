package common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Flow<T>.produce(scope: CoroutineScope) = scope.produce { collect { send(it) } }

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Flow<T>.throttleList(periodMs: Long): Flow<List<T>> {
    val result = mutableListOf<T>()
    val mutex = Mutex()
    return transformLatest { value ->
        mutex.withLock {
            result += value
        }
        delay(periodMs)
        mutex.withLock {
            emit(result.toList())
            result.clear()
        }
    }
}

fun <T> Flow<T>.listTransform(transform: (List<T>) -> List<T>) = flow {
    emitAll(toList().let(transform).asFlow())
}