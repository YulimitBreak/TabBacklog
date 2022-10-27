package core

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@OptIn(ExperimentalCoroutinesApi::class)
internal fun runTest(
    context: CoroutineContext = EmptyCoroutineContext,
    @OptIn(ExperimentalCoroutinesApi::class)
    action: suspend TestScope.() -> Unit,
): TestResult {
    val cleanup = Cleanup()
    return kotlinx.coroutines.test.runTest(
        context + cleanup,
    ) {
        try {
            action.invoke(this)
        } finally {
            cleanup.execute()
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun TestScope.onCleanup(action: suspend () -> Unit) = coroutineContext[Cleanup.Key]!!.invoke(action)

internal class Cleanup : CoroutineContext.Element {

    override val key = Key

    operator fun invoke(action: suspend () -> Unit) {
        callbacks += action
    }

    suspend fun execute() {
        println("Executing cleanup for ${callbacks.size} elements")
        callbacks.asReversed().forEach { it.invoke() }
    }

    private val callbacks = mutableListOf<suspend () -> Unit>()

    companion object Key : CoroutineContext.Key<Cleanup>
}