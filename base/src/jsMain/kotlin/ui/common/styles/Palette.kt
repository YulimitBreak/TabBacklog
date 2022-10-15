package ui.common.styles

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.toCssColor
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.styleModifier
import common.variable
import entity.error.CompositionLocalError
import org.jetbrains.compose.web.css.CSSColorValue

data class Palette(
    val background: Color,
    val primary: Color,
    val accent: Color,
    val onBackground: Color,
    val onPrimary: Color,
    val onAccent: Color,
) {
    @Composable
    fun applyPalette(modifier: Modifier = Modifier, content: @Composable (Modifier) -> Unit) {

        CompositionLocalProvider(
            Local provides this
        ) {
            content(modifier.styleModifier {
                variable(Variable.color_background, background.toCssColor())
                variable(Variable.color_primary, primary.toCssColor())
                variable(Variable.color_accent, accent.toCssColor())
                variable(Variable.color_onBackground, onBackground.toCssColor())
                variable(Variable.color_onPrimary, onPrimary.toCssColor())
                variable(Variable.color_onAccent, onAccent.toCssColor())
            })
        }
    }

    object Variable {
        val color_background by variable<CSSColorValue>()
        val color_primary by variable<CSSColorValue>()
        val color_accent by variable<CSSColorValue>()
        val color_onBackground by variable<CSSColorValue>()
        val color_onPrimary by variable<CSSColorValue>()
        val color_onAccent by variable<CSSColorValue>()
    }

    companion object {

        val Local = compositionLocalOf<Palette> { throw CompositionLocalError("Palette") }

        val primaryColor
            @Composable
            @ReadOnlyComposable
            get() = Local.current.primary

        val accentColor
            @Composable
            @ReadOnlyComposable
            get() = Local.current.accent
    }
}


@Composable
@ReadOnlyComposable
fun Modifier.primaryColors(): Modifier = Palette.Local.current.run { backgroundColor(primary).color(onPrimary) }

@Composable
@ReadOnlyComposable
fun Modifier.accentColors(): Modifier = Palette.Local.current.run { backgroundColor(accent).color(onAccent) }

@Composable
@ReadOnlyComposable
fun Modifier.backgroundColors(): Modifier =
    Palette.Local.current.run { backgroundColor(background).color(onBackground) }