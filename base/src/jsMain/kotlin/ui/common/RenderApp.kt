package ui.common

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.style.KobwebComposeStyleSheet
import com.varabyte.kobweb.silk.InitSilkContext
import com.varabyte.kobweb.silk.SilkStyleSheet
import com.varabyte.kobweb.silk.initSilk
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.DOMScope
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.Element

fun renderApp(
    rootElementId: String,
    additionalInit: (InitSilkContext) -> Unit = {},
    content: @Composable DOMScope<Element>.() -> Unit
) {

    initSilk(additionalInit)
    renderComposable(rootElementId) {
        Style(KobwebComposeStyleSheet)
        Style(SilkStyleSheet)
        content()
    }
}