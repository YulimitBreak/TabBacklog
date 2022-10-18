package ui.common.bookmark

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.silk.components.icons.fa.FaBookBookmark
import com.varabyte.kobweb.silk.components.icons.fa.FaNoteSticky
import org.jetbrains.compose.web.dom.Text
import ui.common.basecomponent.RowButton
import ui.styles.Variants

@Composable
fun BookmarkTypeLibraryButton(
    isInLibrary: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    RowButton(
        onClick = onClick,
        modifier = modifier
            .thenIf(isInLibrary, Variants.Button.SelectedUnclickablePrimary.toModifier())
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
            .thenIf(isInBacklog, Variants.Button.SelectedUnclickablePrimary.toModifier())
    ) {
        FaNoteSticky()
        if (isInBacklog) {
            Text("In backlog")
        } else {
            Text("To backlog")
        }
    }
}