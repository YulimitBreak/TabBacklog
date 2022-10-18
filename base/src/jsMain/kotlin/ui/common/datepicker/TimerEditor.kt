package ui.common.datepicker

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.text.SpanText
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import ui.common.basecomponent.DivText
import ui.common.bookmark.TimerDisplay

@Composable
fun TimerEditor(
    title: String,
    description: String,
    isSelected: Boolean,
    dateTarget: RelativeDateTarget,
    modifier: Modifier = Modifier,
    onEvent: (TimerEditorEvent) -> Unit,
) {
    val date = dateTarget.resolve()
    when {
        isSelected -> TimerDatePicker(title, dateTarget, modifier,
            onCountChange = { onEvent(TimerEditorEvent.OnCountChange(it)) },
            onDateSelect = { onEvent(TimerEditorEvent.OnDateSelect(it)) },
            onModeChange = { onEvent(TimerEditorEvent.OnModeChange(it)) },
            onDelete = { onEvent(TimerEditorEvent.OnDelete) }
        )

        date != null -> TimerDisplay(
            title,
            date,
            modifier,
            onDelete = { onEvent(TimerEditorEvent.OnDelete) })

        else -> {
            Row(
                modifier.gap(8.px).flexWrap(FlexWrap.Nowrap).height(2.5.em)
                    .title(description),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SpanText(
                    title,
                    modifier = Modifier.fontWeight(FontWeight.Bolder).width(25.percent)
                )
                DivText(description, modifier = Modifier.fillMaxSize().fontSize(0.9.em).textAlign(TextAlign.Center))
            }
        }
    }
}

sealed class TimerEditorEvent {
    data class OnCountChange(val count: Int) : TimerEditorEvent()
    data class OnDateSelect(val date: LocalDate?) : TimerEditorEvent()
    data class OnModeChange(val mode: TimerDatePickerMode) : TimerEditorEvent()
    object OnDelete : TimerEditorEvent()
}