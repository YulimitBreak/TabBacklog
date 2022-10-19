package common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Flow<T>.produce(scope: CoroutineScope) = scope.produce { collect { send(it) } }