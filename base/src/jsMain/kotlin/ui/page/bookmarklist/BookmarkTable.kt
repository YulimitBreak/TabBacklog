package ui.page.bookmarklist

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.overflowY
import com.varabyte.kobweb.silk.components.style.toModifier
import entity.Bookmark
import org.w3c.dom.HTMLElement
import ui.common.basecomponent.DivText
import ui.styles.components.TableContentComponent

@Composable
fun BookmarkTable(values: List<Bookmark>, modifier: Modifier, onSelect: (Bookmark) -> Unit, onLoadMore: (() -> Unit)?) {


    Column(
        modifier = TableContentComponent.Style.toModifier()
            .then(modifier)
            .overflowY(Overflow.Auto)
            .attrsModifier {
                if (onLoadMore != null) {
                    onScroll { event ->
                        val element = event.target as? HTMLElement ?: return@onScroll
                        if (element.offsetHeight + element.scrollTop >= element.scrollHeight) {
                            onLoadMore()
                        }
                    }
                }
            }
    ) {
        for (i in 0..1000) {
            DivText(i.toString(), Modifier.fillMaxWidth())
        }
    }
}