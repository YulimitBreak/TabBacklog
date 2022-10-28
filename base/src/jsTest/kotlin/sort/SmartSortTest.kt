package sort

import common.isAfterToday
import core.bookmarkArb
import core.runTest
import core.timeLimit
import entity.sort.SmartSort
import io.kotest.assertions.withClue
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldBeSortedWith
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SmartSortTest {

    @Test
    fun testSmartSort() = runTest {
        val sort = SmartSort

        checkAll(timeLimit, Arb.list(bookmarkArb(), 0..200)) { source ->

            val result = source.sortedWith(sort)
            var currentChecked = result

            withClue("Unreached reminders") {
                val end = currentChecked.takeLastWhile {
                    val remindDate = it.remindDate
                    remindDate != null && remindDate.isAfterToday()
                }
                currentChecked = currentChecked.dropLast(end.size)

                withClue("should be at the end of the list") {
                    currentChecked.forAll {
                        val remindDate = it.remindDate
                        if (remindDate != null) {
                            remindDate.isAfterToday() shouldNotBe true
                        }
                    }
                }
                withClue("should be sorted") {
                    end shouldBeSortedWith { a, b -> a.remindDate!!.compareTo(b.remindDate!!) }
                }
            }

            withClue("Bookmarks with deadlines") {
                val deadlines = currentChecked.takeWhile { it.deadline != null }
                currentChecked = currentChecked.drop(deadlines.size)

                withClue("should be at the start of the list") {
                    currentChecked.forAll {
                        it.deadline.shouldBeNull()
                    }
                }
                withClue("should be sorted") {
                    deadlines shouldBeSortedWith { a, b -> a.deadline!!.compareTo(b.deadline!!) }
                }
            }

            withClue("Bookmarks with reached reminders") {
                val reachedReminders = currentChecked.takeWhile {
                    val remindDate = it.remindDate
                    remindDate != null && !remindDate.isAfterToday()
                }
                currentChecked = currentChecked.drop(reachedReminders.size)

                withClue("should be at the start of the list") {
                    currentChecked.forAll {
                        it.remindDate.shouldBeNull() // unreached should be already filtered out
                    }
                }
                withClue("should be sorted") {
                    reachedReminders shouldBeSortedWith { a, b -> a.remindDate!!.compareTo(b.remindDate!!) }
                }
            }

            withClue("Bookmarks expiring soon") {
                val expiringSoon = currentChecked.takeWhile {
                    val expiration = it.expirationDate
                    expiration != null && !expiration.minus(1, DateTimeUnit.WEEK).isAfterToday()
                }
                currentChecked = currentChecked.drop(expiringSoon.size)

                withClue("should be at the start of the list") {
                    currentChecked.forAll {
                        val expiration = it.expirationDate
                        if (expiration != null) {
                            expiration.minus(1, DateTimeUnit.WEEK).isAfterToday() shouldBe true
                        }
                    }
                }
                withClue("should be sorted") {
                    expiringSoon shouldBeSortedWith { a, b -> a.expirationDate!!.compareTo(b.expirationDate!!) }
                }
            }

            withClue("The rest should be sorted by creation date") {
                currentChecked.filter { it.creationDate != null } shouldBeSortedWith { a, b ->
                    a.creationDate!!.compareTo(
                        b.creationDate!!
                    )
                }
            }
        }
    }
}