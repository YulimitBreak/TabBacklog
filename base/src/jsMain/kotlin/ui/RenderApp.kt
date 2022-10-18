package ui

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.style.KobwebComposeStyleSheet
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributesBuilder
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.silk.InitSilkContext
import com.varabyte.kobweb.silk.SilkStyleSheet
import com.varabyte.kobweb.silk.components.layout.Surface
import com.varabyte.kobweb.silk.initSilk
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.Element
import ui.styles.Palette
import ui.styles.Variants
import ui.styles.components.BookmarkEditClickableArea
import ui.styles.components.SliderComponent
import ui.styles.components.TagComponent

fun renderApp(
    rootElementId: String,
    additionalInit: (InitSilkContext) -> Unit = {},
    content: @Composable DOMScope<Element>.() -> Unit
) {
    val defaultPalette = Palette(
        background = Colors.White,
        primary = Colors.Navy,
        accent = Colors.Cyan,
        onBackground = Colors.Black,
        onPrimary = Colors.White,
        onAccent = Colors.Black,
        warning = Colors.Crimson,
    )
    initSilk {
        with(it.theme) {
            palettes = palettes.copy(
                light = palettes.light.copy(
                    background = defaultPalette.background,
                    color = defaultPalette.onBackground,
                )
            )
            registerComponentVariants(
                Variants.Button.SelectedUnclickablePrimary,
            )
            registerComponentStyle(TagComponent.Style)
            registerComponentVariants(TagComponent.Clickable, TagComponent.Selected)
            registerComponentStyle(BookmarkEditClickableArea.Style)
            registerComponentStyle(SliderComponent.InactiveTrackStyle)
            registerComponentStyle(SliderComponent.TrackStyle)
            registerComponentStyle(SliderComponent.ThumbStyle)
        }
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
            Div(attrs = Modifier.color(defaultPalette.warning).asAttributesBuilder()) {
                P { Text("Error: ") }
                P { Text(e.message ?: "unknown") }
                Pre { Text(e.stackTraceToString()) }
            }
        }
    }
}