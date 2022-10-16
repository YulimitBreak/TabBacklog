package ui.common.styles.components

import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.UserSelect
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.userSelect
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.hover
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.value
import ui.common.styles.Palette

object BookmarkEditClickableArea {
    val Style = ComponentStyle("bookmark-edit-clickable-area") {

        base {
            Modifier
                .border(2.px, LineStyle.Solid, Color.transparent) // Transparent border to avoid jerking on hover
                .borderRadius(8.px)
        }

        hover {
            Modifier
                .userSelect(UserSelect.None)
                .cursor(Cursor.Pointer)
                .border(
                    width = 2.px,
                    LineStyle.Dotted,
                    Palette.Variable.color_primary.value()
                )
                .borderRadius(8.px)
        }
    }
}