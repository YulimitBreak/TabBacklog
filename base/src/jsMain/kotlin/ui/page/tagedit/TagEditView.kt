package ui.page.tagedit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributesBuilder
import com.varabyte.kobweb.compose.ui.graphics.toCssColor
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.icons.fa.FaCheck
import di.ModuleLocal
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.TextInput
import ui.common.basecomponent.RowButton
import ui.common.basecomponent.TagListView
import ui.common.styles.Palette
import ui.common.styles.components.TagComponent

@Composable
fun TagEditView(tags: List<String>, modifier: Modifier, onTagEditEvent: (TagEditEvent) -> Unit) {

    val appModule = ModuleLocal.App.current
    val scope = rememberCoroutineScope()
    val model: TagEditModel =
        remember { appModule.createTagEditModel(scope) }

    Column(modifier = modifier.gap(8.px)) {
        TagListView(tags, modifier = Modifier.fillMaxWidth(), tagModifier = { tag ->
            if (model.selectedTag == tag) {
                TagComponent.Selected.toModifier()
            } else {
                TagComponent.Clickable.toModifier()
                    .onClick { model.selectTag(tag) }
            }
        })

        Row(
            Modifier.fillMaxWidth().flexWrap(FlexWrap.Nowrap).gap(4.px),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextInput(model.editedTag, Modifier.width(100.percent)
                .lineHeight(1.2.em).height(1.2.em)
                .border(0.px)
                .outline(0.px)
                .borderBottom(1.px, LineStyle.Dashed, Palette.primaryColor.toCssColor())
                .asAttributesBuilder {
                    onInput { model.onTagInput(it.value) }
                })
            RowButton(onClick = { model.confirmTag(onTagEditEvent) }, Modifier.size(1.2.em)) {
                FaCheck()
            }
        }
    }
}

sealed class TagEditEvent {
    data class Add(val tag: String) : TagEditEvent()
    data class Edit(val from: String, val to: String) : TagEditEvent()
    data class Delete(val tag: String) : TagEditEvent()
}