package ui.common.datepicker

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributesBuilder
import com.varabyte.kobweb.compose.ui.graphics.toCssColor
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.borderBottom
import com.varabyte.kobweb.compose.ui.modifiers.outline
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import common.DateUtils
import common.styleProperty
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.jetbrains.compose.web.attributes.min
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.DateInput
import ui.common.basecomponent.ButtonCounter
import ui.common.basecomponent.DivText
import ui.styles.Palette

@Composable
fun DatePicker(
    target: DatePickerTarget,
    modifier: Modifier,
    onCountChange: (count: Int) -> Unit,
    onDateSelect: (date: LocalDate?) -> Unit,
) {
    when (target) {
        DatePickerTarget.None -> {
            DivText("None", modifier.textAlign(TextAlign.Center))
        }

        is DatePickerTarget.SetDate -> {
            Box(modifier, contentAlignment = Alignment.Center) {
                DateInput(
                    value = target.date?.toString() ?: "",
                    Modifier
                        .styleProperty("transform", "scale(0.8)")
                        .outline(0.px)
                        .border(0.px)
                        .borderBottom(1.px, LineStyle.Dashed, Palette.primaryColor.toCssColor())
                        .asAttributesBuilder {
                            min(DateUtils.today.plus(DatePeriod(days = 1)).toString())
                            onInput {
                                onDateSelect(if (it.value.isNotBlank()) LocalDate.parse(it.value) else null)
                            }
                        }
                )
            }
        }

        is DatePickerTarget.Counter -> {
            ButtonCounter(target.count, target.timeUnitName, modifier, onCountChange)
        }
    }
}

