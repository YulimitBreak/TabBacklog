package ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributesBuilder
import com.varabyte.kobweb.compose.ui.modifiers.color
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import ui.common.basecomponent.LoadableViewDelegate
import ui.common.basecomponent.LoadingSpinner

@Composable
fun DefaultLocalProvider(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LoadableViewDelegate.Local providesDefault DefaultLoadableViewDelegate()
    ) {
        content()
    }
}

private class DefaultLoadableViewDelegate : LoadableViewDelegate {
    @Composable
    override fun Loading(modifier: Modifier) {
        Box(modifier, contentAlignment = Alignment.Center) {
            LoadingSpinner()
        }
    }

    @Composable
    override fun Error(error: Throwable, modifier: Modifier) {
        Box(
            modifier = modifier, contentAlignment = Alignment.Center
        ) {
            P(
                attrs = Modifier.color(Color.darkred).asAttributesBuilder()
            ) {
                Text(error.message ?: "Unknown error")
            }
        }
    }
}