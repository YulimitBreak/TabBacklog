package ui.page.tagedit

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import ui.common.basecomponent.TagListView
import ui.common.styles.components.TagComponent

@Composable
fun TagEditView(tags: List<String>, modifier: Modifier, onTagEditEvent: (TagEditEvent) -> Unit) {

    Column(modifier = modifier) {
        TagListView(tags, modifier = Modifier.fillMaxWidth(), tagModifier = { tag ->
            TagComponent.Clickable.toModifier()
        })
    }
}

sealed class TagEditEvent {
    data class Add(val tag: String) : TagEditEvent()
    data class Edit(val from: String, val to: String) : TagEditEvent()
    data class Delete(val tag: String) : TagEditEvent()
}