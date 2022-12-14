package ui.common.ext

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun CoroutineScope.request(
    onLoading: (Boolean) -> Unit,
    onError: (Throwable) -> Unit,
    call: suspend () -> Unit,
) {
    launch {
        try {
            onLoading(true)
            call()
        } catch (e: Throwable) {
            if (e is CancellationException) throw e
            onError(e)
        } finally {
            onLoading(false)
        }
    }
}