package entity.sort

import common.CombinableComparator
import entity.Bookmark

abstract class BookmarkSort : CombinableComparator<Bookmark> {

    override val next: BookmarkSort? get() = null

    final override fun compare(a: Bookmark, b: Bookmark): Int {
        return super.compare(a, b)
    }

    fun sort(list: List<Bookmark>) = list.sortedWith(this)

    companion object {
        fun combine(vararg sort: (parent: BookmarkSort?) -> BookmarkSort) =
            sort.foldRight<_, BookmarkSort?>(null) { op, acc ->
                op(acc)
            }
    }
}

val SmartSort = BookmarkSort.combine(
    SortType::UnreachedReminderLast,
    SortType::DeadlineFirst,
    SortType::ReminderFirst,
    SortType::ExpiringSoonFirst,
    { SortType.CreationDate(next = it) },
)