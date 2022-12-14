package ui.common.basecomponent

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.BoxScope
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.id
import com.varabyte.kobweb.compose.ui.modifiers.left
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.compose.ui.modifiers.position
import com.varabyte.kobweb.compose.ui.modifiers.top
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.compose.ui.modifiers.zIndex
import common.safeCast
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.vh
import org.jetbrains.compose.web.css.vw
import org.w3c.dom.HTMLElement

@Composable
fun ModalDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    dialogId: String = "dialog",
    content: @Composable BoxScope.() -> Unit
) {
    if (visible) {
        Box(
            modifier = Modifier
                .id(dialogId)
                .position(Position.Fixed)
                .zIndex(1)
                .left(0.px)
                .top(0.px)
                .width(100.vw)
                .height(100.vh)
                .backgroundColor(Colors.Black.copy(alpha = 128))
                .onClick { it ->
                    if (it.target.safeCast<HTMLElement>()?.id == dialogId) {
                        onDismiss()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}