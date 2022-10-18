package ui.page.collection

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import org.jetbrains.compose.web.css.minus
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import ui.page.bookmarklist.BookmarkList

@Composable
fun CollectionView(modifier: Modifier = Modifier) {

    Column(modifier.role("main").overflowX(Overflow.Auto)) {
        Box(Modifier.fillMaxHeight().minWidth(50.percent), contentAlignment = Alignment.CenterEnd) {

            BookmarkList(
                Modifier.minWidth(400.px)
                    .width(70.percent)
                    .margin(topBottom = 32.px, leftRight = 8.px)
                    .height(100.percent - 64.px)
                    .borderRadius(8.px)
                    .flexShrink(1)
                    .boxShadow(offsetX = 0.px, offsetY = 5.px, blurRadius = 10.px, spreadRadius = 4.px, Colors.DarkGray)
            )
        }
    }
}