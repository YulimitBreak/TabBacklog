package ui.common.bookmark

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.icons.fa.FaFileLines
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import ui.common.basecomponent.DivText
import ui.common.ext.clampLines

@Composable
fun BookmarkMultiTitleView(
    titles: List<String>,
    limit: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier.minWidth(200.px).flexWrap(FlexWrap.Nowrap).gap(16.px),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FaFileLines(Modifier.fontSize(32.px).size(32.px))
        Column(
            Modifier.width(100.percent).gap(2.px),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            val limitedTitles = if (titles.size <= limit) titles else titles.take(limit - 1)
            limitedTitles.forEach { title ->
                DivText(
                    title,
                    modifier = Modifier.width(100.percent).title(title)
                        .clampLines(1)
                        .lineHeight(1.2.em)
                        .maxHeight(2.5.em)
                )
            }
            if (limitedTitles.size < titles.size) {
                DivText(
                    "...and ${titles.size - limitedTitles.size} more",
                    modifier = Modifier.width(100.percent)
                        .opacity(50.percent)
                        .lineHeight(1.2.em)
                        .maxHeight(2.5.em)
                )
            }
        }

    }
}