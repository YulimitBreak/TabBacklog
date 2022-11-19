package ui.page.bookmarklist

import entity.BookmarkType

data class BookmarkSearchViewConfig(
    val searchString: String = "",
    val searchTags: List<String> = emptyList(),
    val preset: Preset = Preset.Smart,
    val typeFirst: BookmarkType? = null,
    val favoriteFirst: Boolean = false,
) {

    sealed interface Preset {
        object Smart : Preset
        data class Alphabetically(val isReversed: Boolean = false) : Preset
        data class CreationDate(val isReversed: Boolean = false) : Preset
    }
}