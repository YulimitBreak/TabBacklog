package entity

data class BookmarkBundle(val bookmarks: List<Bookmark>) {

    val titles = bookmarks.map { it.title }

    val types = bookmarks.mapNotNull { bookmark -> bookmark.type.takeIf { bookmark.isSaved } }.distinct()

    val anySaved = bookmarks.any { it.isSaved }
    val allFavorite = bookmarks.all { it.favorite }

    val commonTags = bookmarks.minBy { it.tags.size }.tags.filter { tag -> bookmarks.all { tag in it.tags } }
    val offTags = bookmarks.flatMap { it.tags - commonTags.toSet() }.distinct()
    val hasTags = commonTags.isNotEmpty() || offTags.isNotEmpty()

    val remindDate = bookmarks.mapNotNull { it.remindDate }.minOrNull()
    val deadline = bookmarks.mapNotNull { it.deadline }.minOrNull()
    val expirationDate = bookmarks.mapNotNull { it.expirationDate }.minOrNull()
}