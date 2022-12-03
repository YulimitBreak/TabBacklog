package ui.common.bookmark

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.silk.components.text.SpanText
import kotlinx.datetime.LocalDate
import ui.styles.brand.DeadlineTimerIcon
import ui.styles.brand.ExpirationTimerIcon
import ui.styles.brand.ReminderTimerIcon

@Composable
fun BookmarkSummaryTimerView(
    title: String,
    remindDate: LocalDate?, deadline: LocalDate?, expirationDate: LocalDate?,
    modifier: Modifier = Modifier,
    onReminderDelete: (() -> Unit)? = null,
    onDeadlineDelete: (() -> Unit)? = null,
    onExpirationDelete: (() -> Unit)? = null
) {
    if (remindDate != null || deadline != null || expirationDate != null) {
        SpanText(title)
        Column(modifier) {
            if (remindDate != null) {
                TimerDisplay(
                    "Reminder", { ReminderTimerIcon() }, remindDate, Modifier.fillMaxWidth(),
                    onDelete = onReminderDelete
                )
            }
            if (deadline != null) {
                TimerDisplay(
                    "Deadline", { DeadlineTimerIcon() }, deadline, Modifier.fillMaxWidth(),
                    onDelete = onDeadlineDelete
                )
            }
            if (expirationDate != null) {
                TimerDisplay(
                    "Expiration", { ExpirationTimerIcon() }, expirationDate, Modifier.fillMaxWidth(),
                    onDelete = onExpirationDelete
                )
            }
        }
    }
}