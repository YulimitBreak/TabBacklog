package ui.page.tagedit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import di.ModuleLocal
import ui.common.basecomponent.TagListView
import ui.common.styles.components.TagComponent

@Composable
fun TagEditView(tags: List<String>, modifier: Modifier, onTagEditEvent: (TagEditEvent) -> Unit) {

    val appModule = ModuleLocal.App.current
    val scope = rememberCoroutineScope()
    val model: TagEditModel =
        remember { appModule.createTagEditModel(scope) }

    Column(modifier = modifier) {
        TagListView(tags, modifier = Modifier.fillMaxWidth(), tagModifier = { tag ->
            if (model.selectedTag == tag) {
                TagComponent.Selected.toModifier()
            } else {
                TagComponent.Clickable.toModifier()
                    .onClick { model.selectTag(tag) }
            }
        })
    }
}

sealed class TagEditEvent {
    data class Add(val tag: String) : TagEditEvent()
    data class Edit(val from: String, val to: String) : TagEditEvent()
    data class Delete(val tag: String) : TagEditEvent()
}