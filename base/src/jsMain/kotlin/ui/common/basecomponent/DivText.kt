package ui.common.basecomponent

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributesBuilder
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.ElementScope
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLElement

@Composable
fun DivText(
    text: String,
    modifier: Modifier = Modifier,
    elementScope: (@Composable ElementScope<HTMLElement>.() -> Unit)? = null,
) {
    Div(attrs = modifier.asAttributesBuilder()) {
        elementScope?.invoke(this)
        Text(text)
    }
}