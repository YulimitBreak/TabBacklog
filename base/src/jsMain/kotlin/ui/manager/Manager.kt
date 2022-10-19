package ui.manager

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.toCssColor
import com.varabyte.kobweb.compose.ui.modifiers.*
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.vh
import ui.common.DefaultLocalProvider
import ui.page.collection.CollectionView
import ui.styles.Palette

@Composable
fun Manager() {

    var page by remember { mutableStateOf(ManagerNavigationPage.COLLECTION) }
    DefaultLocalProvider {
        Column(modifier = Modifier.fillMaxWidth().height(100.vh)) {

            ManagerNavigator(
                page,
                modifier = Modifier.fillMaxWidth().minHeight(64.px)
            ) { page = it }

            when (page) {
                ManagerNavigationPage.COLLECTION ->
                    CollectionView(Modifier.fillMaxSize())

                ManagerNavigationPage.TABS -> {}
                ManagerNavigationPage.TAG_LIST -> {}
                ManagerNavigationPage.AUTO_TAG -> {}
            }

            Box(
                modifier = Modifier.fillMaxWidth().minHeight(32.px).borderTop(
                    4.px, LineStyle.Dashed, Palette.primaryColor.toCssColor()
                )
            )
        }
    }
}