package ui.page.bookmarklist

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributesBuilder
import com.varabyte.kobweb.compose.ui.modifiers.flexWrap
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.silk.components.icons.fa.FaCheck
import entity.BookmarkType
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.minus
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.TextInput
import ui.common.basecomponent.RowButton
import ui.common.basecomponent.Toggle
import ui.page.tagedit.TagEditEvent
import ui.page.tagedit.TagEditView

@Composable
fun BookmarkSearchView(
    searchConfig: BookmarkSearchViewConfig,
    onConfigChange: (SearchViewEvent) -> Unit,
    onApplyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.flexWrap(FlexWrap.Nowrap).gap(8.px)) {
        Column(modifier = Modifier.width(70.percent).gap(8.px), horizontalAlignment = Alignment.Start) {
            TextInput(
                value = searchConfig.searchString,
                attrs = Modifier
                    .width(100.percent - 16.px)
                    .padding(left = 8.px, right = 8.px)
                    .asAttributesBuilder {
                        onInput { onConfigChange(SearchViewEvent.SearchTextUpdate(it.value)) }
                    }
            )
            TagEditView(
                searchConfig.searchTags,
                modifier = Modifier.width(100.percent - 16.px)
                    .padding(left = 8.px, right = 8.px),
            ) { onConfigChange(SearchViewEvent.TagUpdate(it)) }

            Spacer()

            RowButton(onApplyClick) {
                FaCheck()
                Text("Apply")
            }
        }
        Column(modifier = Modifier.gap(8.px), horizontalAlignment = Alignment.Start) {
            BookmarkSortPresetSelector(
                preset = searchConfig.preset,
                onPresetSelect = { onConfigChange(SearchViewEvent.PresetChange(it)) },
                modifier = Modifier.width(100.percent)
            )
            BookmarkSortTypeSelector(searchConfig.typeFirst,
                onTypeSelect = { onConfigChange(SearchViewEvent.TypeFirstChange(it)) }
            )
            Toggle(
                searchConfig.favoriteFirst, "Favorite first",
                onToggle = { onConfigChange(SearchViewEvent.FavoriteFirstChange(it)) }
            )
        }
    }
}


sealed interface SearchViewEvent {

    data class SearchTextUpdate(val text: String) : SearchViewEvent
    data class TagUpdate(val event: TagEditEvent) : SearchViewEvent
    data class PresetChange(val preset: BookmarkSearchViewConfig.Preset) : SearchViewEvent
    data class TypeFirstChange(val type: BookmarkType?) : SearchViewEvent
    data class FavoriteFirstChange(val favoriteFirst: Boolean) : SearchViewEvent
}