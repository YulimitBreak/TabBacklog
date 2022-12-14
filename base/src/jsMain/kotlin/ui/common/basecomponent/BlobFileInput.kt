package ui.common.basecomponent

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributesBuilder
import com.varabyte.kobweb.compose.ui.modifiers.opacity
import com.varabyte.kobweb.compose.ui.modifiers.position
import com.varabyte.kobweb.compose.ui.modifiers.zIndex
import kotlinx.browser.document
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.accept
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.dom.Input
import org.w3c.files.Blob

@Composable
fun BlobFileInput(
    id: String,
    fileTypes: String,
    onFileInput: (Blob) -> Unit,
    content: @Composable (onClick: () -> Unit) -> Unit,
) {
    Input(
        type = InputType.File, Modifier
            .position(Position.Absolute)
            .opacity(0)
            .zIndex(-1)
            .asAttributesBuilder {
                value("")
                id(id)
                accept(fileTypes)
                onInput { event ->
                    if (event.value.isNotBlank()) {
                        onFileInput(document.getElementById(id).asDynamic().files[0] as Blob)
                    }
                }
            }
    )
    content {
        document.getElementById(id)?.asDynamic()?.click()
        Unit
    }
}