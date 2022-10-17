package ui.common.basecomponent

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import common.DateUtils
import entity.core.PluralText
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.jetbrains.compose.web.attributes.min
import org.jetbrains.compose.web.dom.DateInput

@Composable
fun RelativeDatePicker(
    target: RelativeDateTarget,
    modifier: Modifier,
    onCountChange: (count: Int) -> Unit,
    onDateSelect: (date: LocalDate?) -> Unit,
) {
    when (target) {
        RelativeDateTarget.None -> {
            DivText("None", modifier)
        }

        is RelativeDateTarget.SetDate -> {
            Box(modifier, contentAlignment = Alignment.Center) {
                DateInput(
                    value = target.date?.toString() ?: ""
                ) {
                    min(DateUtils.today.plus(DatePeriod(days = 1)).toString())
                    onInput {
                        onDateSelect(if (it.value.isNotBlank()) LocalDate.parse(it.value) else null)
                    }
                }
            }
        }

        is RelativeDateTarget.Counter -> {
            ButtonCounter(target.count, target.timeUnitName, modifier, onCountChange)
        }
    }
}

sealed class RelativeDateTarget {

    object None : RelativeDateTarget()

    data class SetDate(val date: LocalDate?) : RelativeDateTarget()

    data class Counter(val count: Int, val timeUnitName: PluralText?) : RelativeDateTarget()
}