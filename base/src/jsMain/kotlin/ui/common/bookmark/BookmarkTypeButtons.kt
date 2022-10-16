package ui.common.bookmark

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.silk.components.icons.fa.FaBookBookmark
import com.varabyte.kobweb.silk.components.icons.fa.FaNoteSticky
import com.varabyte.kobweb.silk.theme.SilkTheme
import common.styleProperty
import org.jetbrains.compose.web.dom.Text
import ui.common.basecomponent.RowButton
import ui.common.styles.Palette

@Composable
fun BookmarkTypeLibraryButton(
    isInLibrary: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    RowButton(
        onClick = onClick,
        modifier = modifier
            .thenIf(isInLibrary, SelectedBookmarkTypeModifier)
    ) {
        FaBookBookmark()
        if (isInLibrary) {
            Text("In library")
        } else {
            Text("To library")
        }
    }
}

@Composable
fun BookmarkTypeBacklogButton(
    isInBacklog: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    RowButton(
        onClick = onClick,
        modifier = modifier
            .thenIf(isInBacklog, SelectedBookmarkTypeModifier)
    ) {
        FaNoteSticky()
        if (isInBacklog) {
            Text("In backlog")
        } else {
            Text("To backlog")
        }
    }
}


private val SelectedBookmarkTypeModifier
    @Composable
    @ReadOnlyComposable
    get() = Modifier
        .styleProperty("pointer-events", "none")
        .backgroundColor(SilkTheme.palette.background)
        .color(Palette.primaryColor)
        .fontWeight(FontWeight.Lighter)