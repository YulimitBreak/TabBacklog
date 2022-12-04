package ui.page.tablist

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.OverflowWrap
import com.varabyte.kobweb.compose.css.WhiteSpace
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import common.styleProperty
import entity.BookmarkType
import entity.BrowserTab
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.px
import ui.common.basecomponent.DivText
import ui.common.bookmark.Favicon
import ui.common.ext.observeSize
import ui.styles.brand.BacklogIcon
import ui.styles.brand.LibraryIcon

@Composable
fun TabTableItemView(tab: BrowserTab, modifier: Modifier = Modifier) {

    var rowWidth by remember { mutableStateOf(0) }
    Row(
        modifier = modifier.flexWrap(FlexWrap.Nowrap).gap(4.px),
        elementScope = {
            observeSize { width, _ -> rowWidth = width }
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Favicon(tab.favIcon, 16.px)

        DivText(
            tab.title, Modifier.title(tab.title)
                .overflowWrap(OverflowWrap.Anywhere).overflow(Overflow.Hidden)
                .styleProperty("text-overflow", "ellipsis")
                .whiteSpace(WhiteSpace.NoWrap)
                .width((rowWidth * 0.5f).px)
        )

        DivText(
            tab.url, Modifier.title(tab.url)
                .overflowWrap(OverflowWrap.Anywhere).overflow(Overflow.Hidden)
                .styleProperty("text-overflow", "ellipsis")
                .whiteSpace(WhiteSpace.NoWrap)
                .fontWeight(FontWeight.Lighter)
                .fontSize(0.8.em)
                .width((rowWidth * 0.3f).px)
        )
        Spacer()
        tab.bookmark?.let { bookmark ->
            when (bookmark.type) {
                BookmarkType.LIBRARY -> LibraryIcon()
                BookmarkType.BACKLOG -> BacklogIcon()
            }
        }
    }
}