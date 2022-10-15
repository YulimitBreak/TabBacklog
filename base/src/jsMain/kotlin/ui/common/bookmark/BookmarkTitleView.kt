package ui.common.bookmark

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.OverflowWrap
import com.varabyte.kobweb.compose.css.WhiteSpace
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributesBuilder
import com.varabyte.kobweb.compose.ui.modifiers.*
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Img
import ui.common.basecomponent.DivText
import ui.common.ext.styleProperty

@Composable
fun BookmarkTitleView(
    title: String,
    favicon: String?,
    url: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier.minWidth(200.px).maxWidth(400.px).flexWrap(FlexWrap.Nowrap),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!favicon.isNullOrBlank()) {
            Img(src = favicon, attrs = Modifier.size(32.px).margin(right = 16.px).asAttributesBuilder())
        }

        Column(
            Modifier.width(100.percent).gap(8.px),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            DivText(
                title,
                modifier = Modifier.width(100.percent).title(title)
                    .overflowWrap(OverflowWrap.BreakWord).overflow(Overflow.Hidden)
                    .styleProperty("text-overflow", "ellipsis")
                    .lineHeight(1.2.em)
                    .maxHeight(2.4.em)
            )
            DivText(
                url,
                modifier = Modifier.width(200.px).title(url)
                    .whiteSpace(WhiteSpace.NoWrap).overflowWrap(OverflowWrap.Anywhere).overflow(Overflow.Hidden)
                    .fontWeight(FontWeight.Lighter)
                    .fontSize(0.8.em)
                    .styleProperty("text-overflow", "ellipsis")

            )
        }

    }
}