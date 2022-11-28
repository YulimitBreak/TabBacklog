package entity

import kotlinx.datetime.LocalDate

data class EditedBookmarkBundle(
    val base: BookmarkBundle,
    val currentType: BookmarkType? = base.types.singleOrNull(),
    val coreTags: List<String> = base.commonTags,
    val offTags: List<String> = base.offTags,
    val addedTags: List<String> = emptyList(),
    val removedTags: Set<String> = emptySet(),
    val favorite: Boolean? = null,
) {
    val isNew get() = !base.anySaved


    sealed interface TimerDateUpdate {
        object NoUpdate : TimerDateUpdate
        data class Update(val date: LocalDate?) : TimerDateUpdate
    }

}