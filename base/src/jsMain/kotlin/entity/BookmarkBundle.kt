package entity

import common.allHaveSameValue

data class BookmarkBundle(val bookmarks: List<Bookmark>) : List<Bookmark> by bookmarks {

    val titles = bookmarks.map { it.title }

    val types = bookmarks.mapNotNullTo(mutableSetOf()) { bookmark -> bookmark.type.takeIf { bookmark.isSaved } }.toSet()

    val anySaved = bookmarks.any { it.isSaved }
    val allFavorite = bookmarks.all { it.favorite }

    val commonTags = bookmarks.minBy { it.tags.size }.tags.filter { tag -> bookmarks.all { tag in it.tags } }
    val offTags = bookmarks.flatMap { it.tags - commonTags.toSet() }.distinct()
    val hasTags = commonTags.isNotEmpty() || offTags.isNotEmpty()

    val earliestRemindDate = bookmarks.mapNotNull { it.remindDate }.minOrNull()
    val earliestDeadline = bookmarks.mapNotNull { it.deadline }.minOrNull()
    val earliestExpirationDate = bookmarks.mapNotNull { it.expirationDate }.minOrNull()

    val remindDateUndefined = !bookmarks.allHaveSameValue(Bookmark::remindDate)
    val deadlineUndefined = !bookmarks.allHaveSameValue(Bookmark::deadline)
    val expirationDateUndefined = !bookmarks.allHaveSameValue(Bookmark::expirationDate)
}