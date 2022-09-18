package ui.common

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Text

@Composable
fun TextButton(title: String, onClick: () -> Unit) {
    Button(attrs = {
        this.onClick {
            onClick()
        }
    }) {
        Text(title)
    }
}