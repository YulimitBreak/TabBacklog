package ui.common

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.style.KobwebComposeStyleSheet
import com.varabyte.kobweb.silk.InitSilkContext
import com.varabyte.kobweb.silk.SilkStyleSheet
import com.varabyte.kobweb.silk.components.layout.Surface
import com.varabyte.kobweb.silk.initSilk
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.DOMScope
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Pre
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.Element

fun renderApp(
    rootElementId: String,
    additionalInit: (InitSilkContext) -> Unit = {},
    content: @Composable DOMScope<Element>.() -> Unit
) {

    initSilk {
        it.theme.registerComponentVariants(
        )
        additionalInit(it)
    }
    try {
        renderComposable(rootElementId) {
            Style(KobwebComposeStyleSheet)
            Style(SilkStyleSheet)
            Surface {
                content()
            }
        }
    } catch (e: Throwable) {
        e.printStackTrace()
        renderComposable(rootElementId) {
            P {
                Text("Error: ")
            }
            P {
                Text(e.message ?: "unknown")
            }
            Pre {
                Text(e.stackTraceToString())
            }
        }
    }
}