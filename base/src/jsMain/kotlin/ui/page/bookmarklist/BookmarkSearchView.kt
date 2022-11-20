package ui.page.bookmarklist

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributesBuilder
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.width
import entity.BookmarkType
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.TextInput
import ui.common.basecomponent.Toggle
import ui.page.tagedit.TagEditEvent
import ui.page.tagedit.TagEditView

@Composable
fun BookmarkSearchView(
    searchConfig: BookmarkSearchViewConfig,
    onConfigChange: (BookmarkSearchViewEvent) -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(modifier = modifier.gap(8.px)) {
        Text("Search:")
        // TODO clear button
        TextInput(
            value = searchConfig.searchString,
            attrs = Modifier
                .width(100.percent)
                .height(1.5.em)
                .asAttributesBuilder {
                    onInput { onConfigChange(BookmarkSearchViewEvent.SearchTextUpdate(it.value)) }
                }
        )
        // TODO TagSearchView
        TagEditView(
            searchConfig.searchTags,
            modifier = Modifier.width(100.percent),
        ) { onConfigChange(BookmarkSearchViewEvent.TagUpdate(it)) }

        BookmarkSortPresetSelector(
            preset = searchConfig.preset,
            onPresetSelect = { onConfigChange(BookmarkSearchViewEvent.PresetChange(it)) },
            modifier = Modifier.width(100.percent)
        )
        BookmarkSortTypeSelector(searchConfig.typeFirst,
            onTypeSelect = { onConfigChange(BookmarkSearchViewEvent.TypeFirstChange(it)) }
        )
        Toggle(
            searchConfig.favoriteFirst, "Favorite first",
            onToggle = { onConfigChange(BookmarkSearchViewEvent.FavoriteFirstChange(it)) }
        )
    }
}


sealed interface BookmarkSearchViewEvent {

    data class SearchTextUpdate(val text: String) : BookmarkSearchViewEvent
    data class TagUpdate(val event: TagEditEvent) : BookmarkSearchViewEvent
    data class PresetChange(val preset: BookmarkSearchViewConfig.Preset) : BookmarkSearchViewEvent
    data class TypeFirstChange(val type: BookmarkType?) : BookmarkSearchViewEvent
    data class FavoriteFirstChange(val favoriteFirst: Boolean) : BookmarkSearchViewEvent
}