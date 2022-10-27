package core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

@OptIn(ExperimentalCoroutinesApi::class)
internal fun runTest(
    context: CoroutineContext = EmptyCoroutineContext,
    action: suspend CleanupTestScope.() -> Unit,
) = kotlinx.coroutines.test.runTest(
    context,
) {
    val testScope = CleanupTestScope(this)
    try {
        action.invoke(testScope)
    } finally {
        testScope.cleanup()
    }
}

@Suppress("MemberVisibilityCanBePrivate")
@OptIn(ExperimentalCoroutinesApi::class)
internal class CleanupTestScope(
    val testScope: TestScope,
) : CoroutineScope by testScope {


    fun onCleanup(action: suspend () -> Unit) {
        callbacks += action
    }

    suspend fun cleanup() {
        callbacks.asReversed().forEach { it.invoke() }
    }


    // Copied from TestScope

    /**
     * The delay-skipping scheduler used by the test dispatchers running the code in this scope.
     */
    val testScheduler get() = testScope.testScheduler

    /**
     * A scope for background work.
     *
     * This scope is automatically cancelled when the test finishes.
     * Additionally, while the coroutines in this scope are run as usual when
     * using [advanceTimeBy] and [runCurrent], [advanceUntilIdle] will stop advancing the virtual time
     * once only the coroutines in this scope are left unprocessed.
     *
     * Failures in coroutines in this scope do not terminate the test.
     * Instead, they are reported at the end of the test.
     * Likewise, failure in the [TestScope] itself will not affect its [backgroundScope],
     * because there's no parent-child relationship between them.
     *
     * A typical use case for this scope is to launch tasks that would outlive the tested code in
     * the production environment.
     *
     * In this example, the coroutine that continuously sends new elements to the channel will get
     * cancelled:
     * ```
     * @Test
     * fun testExampleBackgroundJob() = runTest {
     *   val channel = Channel<Int>()
     *   backgroundScope.launch {
     *     var i = 0
     *     while (true) {
     *       channel.send(i++)
     *     }
     *   }
     *   repeat(100) {
     *     assertEquals(it, channel.receive())
     *   }
     * }
     * ```
     */
    val backgroundScope: CoroutineScope get() = testScope.backgroundScope

    private val callbacks = mutableListOf<suspend () -> Unit>()


    /**
     * The current virtual time on [testScheduler][TestScope.testScheduler].
     * @see TestCoroutineScheduler.currentTime
     */
    val currentTime: Long get() = testScheduler.currentTime

    /**
     * Advances the [testScheduler][TestScope.testScheduler] to the point where there are no tasks remaining.
     * @see TestCoroutineScheduler.advanceUntilIdle
     */
    fun advanceUntilIdle(): Unit = testScheduler.advanceUntilIdle()

    /**
     * Run any tasks that are pending at the current virtual time, according to
     * the [testScheduler][TestScope.testScheduler].
     *
     * @see TestCoroutineScheduler.runCurrent
     */
    fun runCurrent(): Unit = testScheduler.runCurrent()

    /**
     * Moves the virtual clock of this dispatcher forward by [the specified amount][delayTimeMillis], running the
     * scheduled tasks in the meantime.
     *
     * In contrast with `TestCoroutineScope.advanceTimeBy`, this function does not run the tasks scheduled at the moment
     * [currentTime] + [delayTimeMillis].
     *
     * @throws IllegalStateException if passed a negative [delay][delayTimeMillis].
     * @see TestCoroutineScheduler.advanceTimeBy
     */
    fun advanceTimeBy(delayTimeMillis: Long): Unit = testScheduler.advanceTimeBy(delayTimeMillis)

    /**
     * The [test scheduler][TestScope.testScheduler] as a [TimeSource].
     * @see TestCoroutineScheduler.timeSource
     */
    @OptIn(ExperimentalTime::class)
    val TestScope.testTimeSource: TimeSource get() = testScheduler.timeSource
}
