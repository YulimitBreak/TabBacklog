package ui.common.styles

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.lightened
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

    val primaryLight = primary.lightened(0.3f)
    val primaryDark = primary.darkened(0.3f)
    val accentLight = accent.lightened(0.3f)
    val accentDark = accent.darkened(0.3f)


    @Composable
    fun applyPalette(modifier: Modifier = Modifier, content: @Composable (Modifier) -> Unit) {

        CompositionLocalProvider(
            Local provides this
        ) {
            content(modifier.styleModifier {
                variable(Variable.color_background, background.toCssColor())
                variable(Variable.color_primary, primary.toCssColor())
                variable(Variable.color_primary_light, primaryLight.toCssColor())
                variable(Variable.color_primary_dark, primaryDark.toCssColor())
                variable(Variable.color_accent, accent.toCssColor())
                variable(Variable.color_accent_light, accentLight.toCssColor())
                variable(Variable.color_accent_dark, accentDark.toCssColor())
                variable(Variable.color_onBackground, onBackground.toCssColor())
                variable(Variable.color_onPrimary, onPrimary.toCssColor())
                variable(Variable.color_onAccent, onAccent.toCssColor())
            })
        }
    }

    object Variable {
        private const val PREFIX = "PaletteVariables"
        val color_background by variable<CSSColorValue>(PREFIX)
        val color_primary by variable<CSSColorValue>(PREFIX)
        val color_primary_light by variable<CSSColorValue>(PREFIX)
        val color_primary_dark by variable<CSSColorValue>(PREFIX)
        val color_accent by variable<CSSColorValue>(PREFIX)
        val color_accent_light by variable<CSSColorValue>(PREFIX)
        val color_accent_dark by variable<CSSColorValue>(PREFIX)
        val color_onBackground by variable<CSSColorValue>(PREFIX)
        val color_onPrimary by variable<CSSColorValue>(PREFIX)
        val color_onAccent by variable<CSSColorValue>(PREFIX)
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