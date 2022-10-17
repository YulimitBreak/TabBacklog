package ui.common.basecomponent

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier

@Composable
inline fun <reified E : Enum<E>> EnumSlider(
    value: E,
    modifier: Modifier = Modifier,
    crossinline onValueChange: (E) -> Unit
) {
    val values = enumValues<E>()

    Slider(
        value.ordinal,
        values.size - 1,
        modifier
    ) {
        onValueChange(values[it])
    }
}