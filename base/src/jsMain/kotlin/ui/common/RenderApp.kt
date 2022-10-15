package ui.common

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.style.KobwebComposeStyleSheet
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
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
import ui.common.styles.Palette

fun renderApp(
    rootElementId: String,
    additionalInit: (InitSilkContext) -> Unit = {},
    content: @Composable DOMScope<Element>.() -> Unit
) {
    val defaultPalette = Palette(
        background = Colors.White,
        primary = Colors.DarkBlue,
        accent = Colors.Cyan,
        onBackground = Colors.Black,
        onPrimary = Colors.White,
        onAccent = Colors.Black,
    )
    initSilk {
        it.theme.palettes = it.theme.palettes.copy(
            light = it.theme.palettes.light.copy(
                background = defaultPalette.background,
                color = defaultPalette.onBackground,
            )
        )
        it.theme.registerComponentVariants(
        )
        additionalInit(it)
    }
    try {
        renderComposable(rootElementId) {
            Style(KobwebComposeStyleSheet)
            Style(SilkStyleSheet)
            defaultPalette.applyPalette { modifier ->
                Surface(modifier.fillMaxSize()) {
                    content()
                }
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