package ui.page.bookmarklist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.toCssColor
import com.varabyte.kobweb.compose.ui.modifiers.*
import di.ModuleLocal
import org.jetbrains.compose.web.css.*
import ui.styles.Palette

@Composable
fun BookmarkList(modifier: Modifier = Modifier) {

    val appModule = ModuleLocal.App.current
    val scope = rememberCoroutineScope()
    val model = remember() {
        appModule.createBookmarkListModel(scope)
    }

    Column(modifier.overflow(Overflow.Hidden)) {
        // TODO search

        Box(Modifier.fillMaxWidth().height(400.px).backgroundColor(Color.yellow))

        BookmarkTable(emptyList(),
            Modifier.margin(leftRight = 16.px, topBottom = 16.px).width(100.percent - 32.px)
                .minHeight(20.percent).maxHeight(50.percent)
                .border(1.px, LineStyle.Solid, Palette.Local.current.onBackground.toCssColor()),
            onSelect = {},
            onLoadMore = {
                console.log("Load more!")
            }
        )
    }
}