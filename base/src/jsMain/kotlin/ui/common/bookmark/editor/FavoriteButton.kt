package ui.common.bookmark.editor

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLButtonElement
import ui.common.basecomponent.IconButton
import ui.common.styles.TooltipStyle

@Composable
fun FavoriteButton(
    isFavorite: Boolean,
    popupDirection: TooltipStyle.PopupDirection,
    onChange: (isFavorite: Boolean) -> Unit,
    attrs: AttrBuilderContext<HTMLButtonElement>? = null
) {
    Div {
        IconButton(
            if (isFavorite) "star.svg" else "star-outline.svg",
            "Favorite",
            popupDirection,
            isSolid = false,
            attrs = attrs,
            onClick = { onChange(!isFavorite) })
    }
}