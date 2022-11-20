package ui.page.bookmarklist

import entity.BookmarkType
import entity.sort.BookmarkSort
import entity.sort.SmartSort
import entity.sort.SortType

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

    val compiledSort by lazy {
        var sort: BookmarkSort = when (val preset = preset) {
            is Preset.Alphabetically -> SortType.Alphabetically(isReversed = preset.isReversed)
            is Preset.CreationDate -> SortType.CreationDate(isReversed = preset.isReversed)
            is Preset.Smart -> SmartSort
        }
        sort = when (val type = typeFirst) {
            BookmarkType.LIBRARY -> SortType.LibraryFirst(sort)
            BookmarkType.BACKLOG -> SortType.BacklogFirst(sort)
            null -> sort
        }
        if (favoriteFirst) {
            sort = SortType.FavoriteFirst(sort)
        }
        sort
    }
}