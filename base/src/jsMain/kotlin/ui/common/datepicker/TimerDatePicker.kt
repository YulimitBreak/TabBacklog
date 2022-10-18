package ui.common.datepicker

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.icons.fa.FaXmark
import com.varabyte.kobweb.silk.components.text.SpanText
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import ui.common.basecomponent.EnumSlider

@Composable
fun TimerDatePicker(
    title: String,
    dateTarget: RelativeDateTarget,
    modifier: Modifier = Modifier,
    onCountChange: (Int) -> Unit,
    onDateSelect: (LocalDate?) -> Unit,
    onModeChange: (TimerDatePickerMode) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier.gap(8.px).height(2.5.em).flexWrap(FlexWrap.Nowrap),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SpanText(title, modifier = Modifier.fontWeight(FontWeight.Bolder).width(25.percent))
        Spacer()
        RelativeDatePicker(
            dateTarget,
            modifier = Modifier.width(35.percent).fontSize(0.9.em),
            onCountChange,
            onDateSelect
        )
        EnumSlider(dateTarget.mode, modifier = Modifier.width(20.percent), onModeChange)
        Button(onClick = onDelete, Modifier.size(2.em)) {
            FaXmark(Modifier.fontSize(1.5.em))
        }
    }
}

enum class TimerDatePickerMode {
    NONE,
    SET,
    DAYS,
    WEEKS,
    MONTHS,
    YEARS,
}