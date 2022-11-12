package common

import core.runTest
import core.timeLimit
import io.kotest.assertions.withClue
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldBeSameSizeAs
import io.kotest.matchers.collections.shouldBeSortedWith
import io.kotest.matchers.collections.shouldBeUnique
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.longs.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.numericFloat
import io.kotest.property.checkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.currentTime
import kotlin.math.abs
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FlowOperatorTest {

    @Test
    fun throttleList() = runTest {
        data class Entry(val value: Int, val time: Long)

        checkAll(timeLimit, Arb.long(50, 200), Arb.list(Arb.numericFloat(0f, 2f), 1..50)) { period, delayList ->
            val testFlow = flow {
                delayList.forEachIndexed { index, ratio ->
                    delay((period * ratio).toLong())
                    emit(Entry(index, currentTime))
                }
            }

            val result = testFlow
                .throttleList(period)
                .toList()

            withClue("No elements should be lost") {
                result.flatten() shouldBeSameSizeAs delayList
            }

            result.forEach { list ->
                list shouldBeSortedWith { entry1: Entry, entry2: Entry -> entry1.time.compareTo(entry2.time) }
                list.shouldBeUnique()
                withClue("Each element in the list should predate next one for not longer than period") {
                    list.windowed(2).forEach { (entry1, entry2) ->
                        abs(entry2.time - entry1.time) shouldBeLessThan period
                    }
                }
            }

            result.windowed(2).forEach { (list1, list2) ->
                withClue(
                    "Time difference between two separate lists should be more than period"
                ) {
                    list2.minBy { it.time }.time - list1.maxBy { it.time }.time shouldBeGreaterThanOrEqual period
                }
            }
        }
    }

    @Test
    fun listTransform_noTransform() = runTest {
        checkAll(timeLimit, Arb.list(Arb.int(0..1000))) { source ->
            val flow = flow {
                source.forEach {
                    emit(it)
                    delay(100)
                }
            }
            val result = flow.listTransform { it }.toList()
            result shouldBe source
        }
    }

    @Test
    fun listTransform_sort() = runTest {
        checkAll(timeLimit, Arb.list(Arb.int(0..1000))) { source ->
            val flow = flow {
                source.forEach {
                    emit(it)
                    delay(100)
                }
            }
            val result = flow.listTransform { it.sorted() }.toList()
            result shouldBeSameSizeAs source
            result shouldBeSortedWith naturalOrder()
        }
    }

    @Test
    fun listTransform_filter() = runTest {
        checkAll(timeLimit, Arb.list(Arb.int(0..1000)), Arb.int(100..900)) { source, bar ->
            val flow = flow {
                source.forEach {
                    emit(it)
                    delay(100)
                }
            }
            val result = flow.listTransform { list -> list.filter { it > bar } }.toList()
            result.forAll {
                it shouldBeGreaterThan bar
            }
            (source - result.toSet()).forAll {
                it shouldBeLessThanOrEqual bar
            }
        }
    }
}