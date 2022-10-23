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
import common.styleProperty
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.TextArea
import ui.common.basecomponent.DivText

@Composable
fun BookmarkTitleView(
    title: String,
    favicon: String?,
    url: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier.minWidth(200.px).maxWidth(400.px).flexWrap(FlexWrap.Nowrap).gap(16.px),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Favicon(favicon, 32.px)
        Column(
            Modifier.width(100.percent).gap(8.px),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            DivText(
                title,
                modifier = Modifier.width(100.percent).title(title)
                    .overflowWrap(OverflowWrap.Anywhere).overflow(Overflow.Hidden)
                    .styleProperty("text-overflow", "ellipsis")
                    .lineHeight(1.2.em)
                    .maxHeight(2.5.em)
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

@Composable
fun BookmarkTitleEdit(
    title: String,
    favicon: String?,
    onInput: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.gap(16.px).flexWrap(FlexWrap.Nowrap),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Favicon(favicon, 32.px)

        TextArea(title, Modifier.lineHeight(1.2.em).fillMaxWidth().height(3.6.em)
            .styleProperty("resize", "none")
            .asAttributesBuilder {
                onInput { onInput(it.value) }
            })
    }
}

