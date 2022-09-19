package ui.common

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLButtonElement

@Composable
fun TextButton(title: String, attrs: AttrBuilderContext<HTMLButtonElement>? = null, onClick: () -> Unit) {
    Button(attrs = {
        this.onClick {
            onClick()
        }
        attrs?.invoke(this)
    }) {
        Text(title)
    }
}