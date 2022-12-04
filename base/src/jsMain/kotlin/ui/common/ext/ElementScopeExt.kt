package ui.common.ext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import common.js.ResizeObserver
import org.jetbrains.compose.web.dom.ElementScope
import org.w3c.dom.HTMLElement

@Composable
fun ElementScope<HTMLElement>.observeSize(onResize: (width: Int, height: Int) -> Unit) {
    DisposableEffect(this) {
        val resizeObserver = ResizeObserver {
            onResize(scopeElement.clientWidth, scopeElement.clientHeight)
        }
        resizeObserver.observe(scopeElement)
        onDispose {
            resizeObserver.disconnect()
        }
    }
}