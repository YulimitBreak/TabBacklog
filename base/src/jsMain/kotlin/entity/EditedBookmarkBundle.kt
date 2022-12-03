package entity

data class EditedBookmarkBundle(
    val base: BookmarkBundle,
    val currentType: BookmarkType? = base.types.singleOrNull(),
    val coreTags: List<String> = base.commonTags,
    val offTags: List<String> = base.offTags,
    val addedTags: List<String> = emptyList(),
    val removedTags: Set<String> = emptySet(),
    val favorite: Boolean? = null,
    val reminderUnset: Boolean = base.remindDateUndefined,
    val deadlineUnset: Boolean = base.deadlineUndefined,
    var expirationUnset: Boolean = base.expirationDateUndefined,
) {
    val isNew get() = !base.anySaved

    val tags = (coreTags - removedTags) + addedTags
}