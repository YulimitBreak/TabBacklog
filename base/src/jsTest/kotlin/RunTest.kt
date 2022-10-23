import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlin.js.Promise

@OptIn(DelicateCoroutinesApi::class)
internal fun runTest(
    action: suspend TestScope.() -> Unit,
): Promise<Unit> = GlobalScope.promise {
    val testScope = TestScope(this)
    try {
        action.invoke(testScope)
    } finally {
        testScope.cleanup()
    }
}

internal class TestScope(
    inner: CoroutineScope,
) : CoroutineScope by inner {

    private val callbacks = mutableListOf<suspend () -> Unit>()

    fun onCleanup(action: suspend () -> Unit) {
        callbacks += action
    }

    suspend fun cleanup() {
        callbacks.asReversed().forEach { it.invoke() }
    }
}
