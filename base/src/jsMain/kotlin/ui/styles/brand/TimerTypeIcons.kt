package ui.styles.brand

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.silk.components.icons.fa.FaBell
import com.varabyte.kobweb.silk.components.icons.fa.FaSkull
import com.varabyte.kobweb.silk.components.icons.fa.FaThumbtack
import com.varabyte.kobweb.silk.components.icons.fa.IconStyle

@Composable
fun ReminderTimerIcon(modifier: Modifier = Modifier) {
    FaBell(modifier = modifier, style = IconStyle.FILLED)
}

@Composable
fun DeadlineTimerIcon(modifier: Modifier = Modifier) {
    FaThumbtack(modifier = modifier)
}

@Composable
fun ExpirationTimerIcon(modifier: Modifier = Modifier) {
    FaSkull(modifier = modifier)
}