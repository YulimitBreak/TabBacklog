package ui.common.bookmark

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.icons.fa.FaXmark
import com.varabyte.kobweb.silk.components.text.SpanText
import common.DateUtils
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import ui.common.styles.Palette

@Composable
fun TimerDisplay(
    title: String,
    date: LocalDate,
    modifier: Modifier,
    onDelete: (() -> Unit)? = null
) {
    Row(modifier.gap(8.px).justifyContent(JustifyContent.Stretch), verticalAlignment = Alignment.CenterVertically) {
        SpanText(title, modifier = Modifier.fontWeight(FontWeight.Bolder).textAlign(TextAlign.Center))
        Spacer()
        SpanText(DateUtils.Formatter.DmySlash(date))
        SpanText(
            DateUtils.formatTimeRelation(date),
            modifier =
            Modifier.width(30.percent).fontWeight(FontWeight.Lighter).textAlign(TextAlign.Center).thenIf(
                DateUtils.today.toEpochDays() >= date.toEpochDays(),
                Modifier.color(Palette.warningColor)
            )
        )
        if (onDelete != null) {
            Button(onClick = onDelete, Modifier.size(2.em)) {
                FaXmark(Modifier.fontSize(1.5.em))
            }
        }
    }
}