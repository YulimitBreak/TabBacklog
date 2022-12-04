package ui.common.ext

import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.OverflowWrap
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.display
import com.varabyte.kobweb.compose.ui.modifiers.overflow
import com.varabyte.kobweb.compose.ui.modifiers.overflowWrap
import common.styleProperty
import org.jetbrains.compose.web.css.DisplayStyle

fun Modifier.clampLines(count: Int) = this
    .display(DisplayStyle("-webkit-box"))
    .styleProperty("-webkit-line-clamp", count)
    .styleProperty("-webkit-box-orient", "vertical")
    .overflowWrap(OverflowWrap.Anywhere).overflow(Overflow.Hidden)
    .styleProperty("text-overflow", "ellipsis")