package common

import kotlinx.coroutines.channels.ReceiveChannel

suspend fun <T> ReceiveChannel<T>.receive(count: Int): List<T> {
    val result = mutableListOf<T>()
    repeat(count) {
        val channelResult = receiveCatching()
        when {
            channelResult.isSuccess -> result.add(channelResult.getOrThrow())
            channelResult.isClosed -> return result
            channelResult.isFailure -> throw channelResult.exceptionOrNull() ?: return result
        }
    }
    return result
}