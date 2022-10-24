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
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.icons.fa.FaBookmark
import com.varabyte.kobweb.silk.components.icons.fa.FaStar
import com.varabyte.kobweb.silk.components.icons.fa.IconStyle
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.components.text.SpanText
import common.styleProperty
import entity.Bookmark
import entity.BookmarkType
import org.jetbrains.compose.web.css.*
import ui.common.basecomponent.DivText
import ui.styles.components.TagComponent

@Composable
fun BookmarkTableView(
    bookmark: Bookmark,
    tagModifier: @Composable (tag: String) -> Modifier = { Modifier },
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.gap(8.px).flexWrap(FlexWrap.Nowrap), verticalAlignment = Alignment.CenterVertically) {
        Favicon(bookmark.favicon, 24.px)
        Column(
            Modifier.width(100.percent - 8.px).gap(4.px),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                Modifier.width(100.percent).flexWrap(FlexWrap.Nowrap).gap(4.px),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (bookmark.type == BookmarkType.LIBRARY) {
                    FaBookmark(Modifier.fontSize(1.2.em), IconStyle.FILLED)
                }
                DivText(
                    bookmark.title,
                    modifier = Modifier.title(bookmark.title)
                        .overflowWrap(OverflowWrap.Anywhere).overflow(Overflow.Hidden)
                        .styleProperty("text-overflow", "ellipsis")
                        .lineHeight(1.2.em)
                        .maxHeight(1.2.em)
                        .flexShrink(1)
                )
                if (bookmark.favorite) {
                    FaStar(Modifier.fontSize(1.1.em), IconStyle.FILLED)
                }
                Spacer()

            }
            Row(
                Modifier.width(100.percent).flexWrap(FlexWrap.Nowrap)
                    .gap(2.px),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DivText(
                    bookmark.url,
                    modifier = Modifier.width(200.px).title(bookmark.url)
                        .whiteSpace(WhiteSpace.NoWrap).overflowWrap(OverflowWrap.Anywhere).overflow(Overflow.Hidden)
                        .fontWeight(FontWeight.Lighter)
                        .fontSize(0.8.em)
                        .styleProperty("text-overflow", "ellipsis")
                )
                Spacer()
                bookmark.tags.take(3).forEach { tag ->
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