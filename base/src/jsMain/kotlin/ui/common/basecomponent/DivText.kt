package ui.common.basecomponent

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributesBuilder
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Composable
fun DivText(text: String, modifier: Modifier = Modifier) {
    Div(attrs = modifier.asAttributesBuilder()) {
        Text(text)
    }
}