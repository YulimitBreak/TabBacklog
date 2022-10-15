package ui.common.basecomponent

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.css.px

@Composable
fun TagListView(
    tags: List<String>,
    modifier: Modifier = Modifier,
    tagModifier: (tag: String) -> Modifier = { Modifier },
) {

    Row(modifier.gap(2.px)) {
        tags.forEach { tag ->
            key(tag) {
                SpanText(tag, modifier = tagModifier(tag))
            }
        }
    }
}