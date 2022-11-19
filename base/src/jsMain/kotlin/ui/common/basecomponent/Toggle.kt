package ui.common.basecomponent

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.silk.components.icons.fa.FaToggleOff
import com.varabyte.kobweb.silk.components.icons.fa.FaToggleOn
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Text

@Composable
fun Toggle(isToggledOn: Boolean, text: String, onToggle: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier.gap(4.px)
        .onClick { onToggle(!isToggledOn) }
        .cursor(Cursor.Pointer)
    ) {
        if (isToggledOn) {
            FaToggleOn()
        } else {
            FaToggleOff()
        }
        Text(text)
    }
}
