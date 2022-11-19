package ui.styles.brand

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.silk.components.icons.fa.FaBookBookmark
import com.varabyte.kobweb.silk.components.icons.fa.FaNoteSticky

@Composable
fun LibraryIcon(modifier: Modifier = Modifier) {
    FaBookBookmark(modifier = modifier)
}

@Composable
fun BacklogIcon(modifier: Modifier = Modifier) {
    FaNoteSticky(modifier = modifier)
}