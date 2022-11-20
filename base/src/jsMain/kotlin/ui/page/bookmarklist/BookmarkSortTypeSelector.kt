package ui.page.bookmarklist

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.text.SpanText
import entity.BookmarkType
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.px
import ui.styles.Palette
import ui.styles.brand.BacklogIcon
import ui.styles.brand.LibraryIcon

@Composable
fun BookmarkSortTypeSelector(
    selectedType: BookmarkType?,
    onTypeSelect: (BookmarkType?) -> Unit,
    modifier: Modifier = Modifier,
) {

    fun presetBasedColor(type: BookmarkType, on: Color, off: Color) =
        if (selectedType == type) on else off

    val palette = Palette.Local.current
    fun Modifier.setup(
        type: BookmarkType,
        radiusLeft: Boolean = false,
        radiusRight: Boolean = false,
    ): Modifier =
        backgroundColor(presetBasedColor(type, palette.accent, palette.primary))
            .color(presetBasedColor(type, palette.onAccent, palette.onPrimary))
            .cursor(Cursor.Pointer)
            .padding(8.px)
            .borderRadius(
                topLeft = if (radiusLeft) 8.px else 0.px,
                bottomLeft = if (radiusLeft) 8.px else 0.px,
                topRight = if (radiusRight) 8.px else 0.px,
                bottomRight = if (radiusRight) 8.px else 0.px,
            )
            .onClick {
                if (selectedType == type) onTypeSelect(null) else onTypeSelect(type)
            }

    Row(modifier = modifier.flexWrap(FlexWrap.Nowrap).gap(4.px), horizontalArrangement = Arrangement.Center) {
        Row(
            modifier = Modifier.setup(BookmarkType.LIBRARY, radiusLeft = true)
        ) {
            LibraryIcon()
            SpanText(
                "Library first",
                Modifier
            )
        }

        Row(
            modifier = Modifier.setup(BookmarkType.BACKLOG, radiusRight = true)
        ) {
            BacklogIcon()
            SpanText(
                "Backlog first",
                Modifier
            )
        }
    }
}