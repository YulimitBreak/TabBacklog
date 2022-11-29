package ui.page.editor

import androidx.compose.runtime.mutableStateOf
import kotlinx.datetime.LocalDate
import ui.common.bookmark.TimerEditorEvent
import ui.common.datepicker.DatePickerMode
import ui.common.datepicker.DatePickerTarget

class TimerEditorDelegate(
    val stateDateGenerator: () -> LocalDate?
) {

    private val state = mutableStateOf<TimerState?>(null)

    fun applyTimer(action: (state: LocalDate?) -> Unit) = state.value?.let {
        action(it.toDatePickerTarget().resolve())
    }

    fun onTimerEvent(event: TimerEditorEvent) {
        state.value = when (event) {
            is TimerEditorEvent.OnCountChange -> timerState.copy(count = event.count.coerceAtLeast(1))
            is TimerEditorEvent.OnDateSelect -> timerState.copy(rememberedDate = event.date)
            is TimerEditorEvent.OnDelete -> timerState.copy(rememberedDate = null, selectedMode = DatePickerMode.NONE)
            is TimerEditorEvent.OnModeChange -> timerState.copy(selectedMode = event.mode)
        }
    }

    val timerTarget get() = timerState.toDatePickerTarget()

    private val timerState: TimerState
        get() {
            state.value?.let { return it }
            val date = stateDateGenerator()
            return if (date != null) {
                TimerState(date, 1, DatePickerMode.SET)
            } else {
                TimerState(null, 1, DatePickerMode.NONE)
            }
        }

    private data class TimerState(
        val rememberedDate: LocalDate?,
        val count: Int,
        val selectedMode: DatePickerMode,
    ) {
        fun toDatePickerTarget() = when (selectedMode) {
            DatePickerMode.NONE -> DatePickerTarget.None
            DatePickerMode.SET -> DatePickerTarget.SetDate(rememberedDate)
            DatePickerMode.DAYS -> DatePickerTarget.Counter.Days(count)
            DatePickerMode.WEEKS -> DatePickerTarget.Counter.Weeks(count)
            DatePickerMode.MONTHS -> DatePickerTarget.Counter.Months(count)
            DatePickerMode.YEARS -> DatePickerTarget.Counter.Years(count)
        }
    }
}