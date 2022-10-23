package ui.common.bookmark

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributesBuilder
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.silk.components.icons.fa.FaFileLines
import org.jetbrains.compose.web.css.CSSNumeric
import org.jetbrains.compose.web.dom.Img

@Composable
fun Favicon(favicon: String?, size: CSSNumeric, modifier: Modifier = Modifier) {
    if (!favicon.isNullOrBlank()) {
        Img(src = favicon, attrs = Modifier.size(size).then(modifier).asAttributesBuilder())
    } else {
        FaFileLines(Modifier.fontSize(size).size(size).then(modifier))
    }
}