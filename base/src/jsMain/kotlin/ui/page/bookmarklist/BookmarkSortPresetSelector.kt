package ui.page.bookmarklist

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.icons.fa.FaSort
import com.varabyte.kobweb.silk.components.icons.fa.FaSortDown
import com.varabyte.kobweb.silk.components.icons.fa.FaSortUp
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import ui.styles.Palette

@Composable
fun BookmarkSortPresetSelector(
    preset: BookmarkSearchViewConfig.Preset,
    onPresetSelect: (BookmarkSearchViewConfig.Preset) -> Unit,
    modifier: Modifier = Modifier,
) {

    inline fun <reified Preset : BookmarkSearchViewConfig.Preset> presetBasedColor(on: Color, off: Color) =
        if (preset is Preset) on else off

    val palette = Palette.Local.current
    inline fun <reified Preset : BookmarkSearchViewConfig.Preset> Modifier.setup(
        roundedLeft: Boolean = false,
        roundedRight: Boolean = false,
    ): Modifier =
        backgroundColor(presetBasedColor<Preset>(palette.accent, palette.primary))
            .color(presetBasedColor<Preset>(palette.onAccent, palette.onPrimary))
            .cursor(Cursor.Pointer)
            .padding(8.px)
            .gap(8.px)
            .justifyContent(JustifyContent.Center)
            .borderRadius(
                topLeft = if (roundedLeft) 8.px else 0.px,
                bottomLeft = if (roundedLeft) 8.px else 0.px,
                topRight = if (roundedRight) 8.px else 0.px,
                bottomRight = if (roundedRight) 8.px else 0.px,
            ).width(30.percent)

    Row(modifier = modifier.flexWrap(FlexWrap.Nowrap).gap(4.px), horizontalArrangement = Arrangement.Center) {
        Row(
            modifier = Modifier.setup<BookmarkSearchViewConfig.Preset.Smart>(roundedLeft = true)
                .onClick { onPresetSelect(BookmarkSearchViewConfig.Preset.Smart) }
        ) {
            SpanText(
                "Smart",
                Modifier
            )
        }
        Row(
            modifier = Modifier.setup<BookmarkSearchViewConfig.Preset.Alphabetically>()
                .onClick {
                    onPresetSelect(
                        if (preset is BookmarkSearchViewConfig.Preset.Alphabetically)
                            BookmarkSearchViewConfig.Preset.Alphabetically(!preset.isReversed)
                        else BookmarkSearchViewConfig.Preset.Alphabetically()
                    )
                }
        ) {
            if (preset is BookmarkSearchViewConfig.Preset.Alphabetically)
                if (preset.isReversed) {
                    FaSortUp()
                } else {
                    FaSortDown()
                }
            else {
                FaSort()
            }
            SpanText(
                "Alphabetically",
            )
        }
        Row(
            modifier = Modifier.setup<BookmarkSearchViewConfig.Preset.CreationDate>(roundedRight = true)
                .onClick {
                    onPresetSelect(
                        if (preset is BookmarkSearchViewConfig.Preset.CreationDate)
                            BookmarkSearchViewConfig.Preset.CreationDate(!preset.isReversed)
                        else BookmarkSearchViewConfig.Preset.CreationDate()
                    )
                }
        ) {
            if (preset is BookmarkSearchViewConfig.Preset.CreationDate)
                if (preset.isReversed) {
                    FaSortUp()
                } else {
                    FaSortDown()
                }
            else {
                FaSort()
            }
            SpanText(
                "By Creation Date",
            )
        }
    }
}