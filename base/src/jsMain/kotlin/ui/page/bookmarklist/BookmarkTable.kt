package ui.page.bookmarklist

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.overflowY
import com.varabyte.kobweb.silk.components.style.toModifier
import entity.Bookmark
import ui.common.basecomponent.DivText
import ui.styles.components.TableContentComponent

@Composable
fun BookmarkTable(values: List<Bookmark>, modifier: Modifier, onSelect: (Bookmark) -> Unit, onLoadMore: () -> Unit) {

    Column(
        TableContentComponent.Style.toModifier()
            .then(modifier)
            .overflowY(Overflow.Auto)
    ) {
        for (i in 0..1000) {
            DivText(i.toString(), Modifier.fillMaxWidth())
        }
    }
}