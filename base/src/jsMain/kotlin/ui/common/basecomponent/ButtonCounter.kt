package ui.common.basecomponent

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.silk.components.icons.fa.FaMinus
import com.varabyte.kobweb.silk.components.icons.fa.FaPlus
import com.varabyte.kobweb.silk.components.text.SpanText
import entity.core.PluralText
import org.jetbrains.compose.web.css.em

@Composable
fun ButtonCounter(count: Int, suffix: PluralText? = null, modifier: Modifier = Modifier, onCountChange: (Int) -> Unit) {

    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
        RowButton(
            onClick = { onCountChange(count - 1) },
            modifier = Modifier.size(1.2.em)
        ) { FaMinus() }

        if (suffix == null) {
            SpanText(count.toString())
        } else {
            SpanText(("$count ${suffix.pluralize(count)}"))
        }

        RowButton(
            onClick = { onCountChange(count + 1) },
            modifier = Modifier.size(1.2.em)
        ) { FaPlus() }
    }
}