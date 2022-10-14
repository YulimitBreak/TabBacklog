package ui.common.bookmark

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement
import ui.common.basecomponent.RelativeDatePicker
import ui.common.basecomponent.RelativeDatePickerState
import ui.common.styles.UtilStyle

@Composable
fun BookmarkTimerPanel(
    state: BookmarkTimerPanelState,
    attrs: (AttrsScope<HTMLDivElement>.() -> Unit)? = null,
    onStateChanged: (BookmarkTimerPanelState) -> Unit,
) {

    Div(
        attrs = {
            attrs?.invoke(this)
            style {
                display(DisplayStyle.Grid)
                gridTemplateColumns("2fr 3fr")
                gridTemplateRows("1fr 1fr 1fr")
            }
        }
    ) {
        Div(
            attrs = {
                title("For links that will be hidden in the list until reminder date")
                classes(UtilStyle.centerContent)
                style {
                    gridRow("1/2")
                    gridColumn("1/2")
                }
            }
        ) {
            Text("Reminder")
        }
        RelativeDatePicker(
            state.reminder,
            attrs = {
                style {
                    gridRow("1/2")
                    gridColumn("2/3")
                }
            },
            onStateUpdate = { onStateChanged(state.copy(reminder = it)) }
        )
        Div(
            attrs = {
                title("For links that represent tasks that need to be done by specific date")
                classes(UtilStyle.centerContent)
                style {
                    gridRow("2/3")
                    gridColumn("1/2")
                }
            }
        ) {
            Text("Deadline")
        }
        RelativeDatePicker(
            state.deadline,
            attrs = {
                style {
                    gridRow("2/3")
                    gridColumn("2/3")
                }
            },
            onStateUpdate = { onStateChanged(state.copy(deadline = it)) }
        )
        Div(
            attrs = {
                title("For links that should be deleted after specific date")
                classes(UtilStyle.centerContent)
                style {
                    gridRow("3/4")
                    gridColumn("1/2")
                }
            }
        ) {
            Text("Expiration")
        }
        RelativeDatePicker(
            state.expiration,
            attrs = {
                style {
                    gridRow("3/4")
                    gridColumn("2/3")
                }
            },
            onStateUpdate = { onStateChanged(state.copy(expiration = it)) }
        )
    }
}

@Composable
fun rememberBookmarkTimerPanelState(
    initialDeadline: LocalDate?,
    initialReminder: LocalDate?,
    initialExpiration: LocalDate?
) = remember {
    mutableStateOf(
        BookmarkTimerPanelState.fromInitialDate(initialDeadline, initialReminder, initialExpiration)
    )
}

data class BookmarkTimerPanelState(
    val deadline: RelativeDatePickerState,
    val reminder: RelativeDatePickerState,
    val expiration: RelativeDatePickerState,
) {

    val hasTimers get() = deadline.isValid || reminder.isValid || expiration.isValid

    companion object {
        fun fromInitialDate(
            initialDeadline: LocalDate?,
            initialReminder: LocalDate?,
            initialExpiration: LocalDate?
        ) = BookmarkTimerPanelState(
            deadline = RelativeDatePickerState.fromInitialDate(initialDeadline),
            reminder = RelativeDatePickerState.fromInitialDate(initialReminder),
            expiration = RelativeDatePickerState.fromInitialDate(initialExpiration)
        )
    }
}