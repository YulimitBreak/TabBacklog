package ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributesBuilder
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import data.BrowserInteractor
import di.AppModule
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import ui.common.basecomponent.LoadableViewDelegate
import ui.common.basecomponent.LoadingSpinner
import ui.styles.Palette

@Composable
fun DefaultLocalProvider(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LoadableViewDelegate.Local providesDefault DefaultLoadableViewDelegate(),
        BrowserInteractor.Local providesDefault AppModule.Local.current.repositoryModule.browserInteractor,
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
                attrs = Modifier.color(Palette.warningColor).textAlign(TextAlign.Center).asAttributesBuilder()
            ) {
                Text(error.message ?: "Unknown error")
            }
        }
    }
}