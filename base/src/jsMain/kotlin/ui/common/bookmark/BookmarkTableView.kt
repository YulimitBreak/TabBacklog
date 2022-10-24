package ui.common.bookmark

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.OverflowWrap
import com.varabyte.kobweb.compose.css.WhiteSpace
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.icons.fa.FaStar
import com.varabyte.kobweb.silk.components.icons.fa.IconStyle
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.components.text.SpanText
import common.styleProperty
import org.jetbrains.compose.web.css.*
import ui.common.basecomponent.DivText
import ui.styles.components.TagComponent

@Composable
fun BookmarkTableView(
    title: String,
    favicon: String?,
    url: String,
    isFavorite: Boolean,
    tags: List<String>,
    tagModifier: @Composable (tag: String) -> Modifier = { Modifier },
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.gap(8.px).flexWrap(FlexWrap.Nowrap), verticalAlignment = Alignment.CenterVertically) {
        Favicon(favicon, 24.px)
        Column(
            Modifier.width(100.percent - 8.px).gap(4.px),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                Modifier.width(100.percent).flexWrap(FlexWrap.Nowrap).gap(4.px),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DivText(
                    title,
                    modifier = Modifier.width(100.percent - 16.px).title(title)
                        .overflowWrap(OverflowWrap.Anywhere).overflow(Overflow.Hidden)
                        .styleProperty("text-overflow", "ellipsis")
                        .lineHeight(1.2.em)
                        .maxHeight(1.2.em)
                )
                if (isFavorite) {
                    FaStar(Modifier.size(16.px), IconStyle.FILLED)
                }
            }
            Row(
                Modifier.width(100.percent).flexWrap(FlexWrap.Nowrap).justifyContent(JustifyContent.SpaceBetween),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DivText(
                    url,
                    modifier = Modifier.width(200.px).title(url)
                        .whiteSpace(WhiteSpace.NoWrap).overflowWrap(OverflowWrap.Anywhere).overflow(Overflow.Hidden)
                        .fontWeight(FontWeight.Lighter)
                        .fontSize(0.8.em)
                        .styleProperty("text-overflow", "ellipsis")
                )
                Row(Modifier.flexWrap(FlexWrap.Nowrap).gap(2.px), verticalAlignment = Alignment.CenterVertically) {
                    tags.take(3).forEach { tag ->
                        key(tag) {
                            SpanText(
                                tag,
                                TagComponent.Style.toModifier().then(tagModifier(tag))
                            )
                        }
                    }
                }
            }
        }
    }
}