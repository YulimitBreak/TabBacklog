package ui.manager

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.toCssColor
import com.varabyte.kobweb.compose.ui.modifiers.*
import org.jetbrains.compose.web.css.*
import ui.page.collection.CollectionView
import ui.styles.Palette

@Composable
fun Manager() {

    var page by remember { mutableStateOf(ManagerNavigationPage.COLLECTION) }

    Column(modifier = Modifier.fillMaxWidth().height(100.vh - 16.px).justifyContent(JustifyContent.Normal)) {

        ManagerNavigator(
            page,
            modifier = Modifier.fillMaxWidth().minHeight(64.px)
        ) { page = it }

        when (page) {
            ManagerNavigationPage.COLLECTION ->
                CollectionView(Modifier.flexGrow(1).fillMaxWidth())

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