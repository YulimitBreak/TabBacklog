package ui.common.basecomponent

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier

@Composable
fun <T> ListSlider(
    value: T,
    options: List<T>,
    modifier: Modifier = Modifier,
    onValueChange: (T) -> Unit
) {

    Slider(
        options.indexOf(value).coerceAtLeast(0),
        options.size - 1,
        modifier
    ) {
        onValueChange(options[it])
    }
}