package ui.common.basecomponent

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.asAttributesBuilder
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Span

@Composable
fun T(text: String, modifier: Modifier = Modifier, wrapperType: TextWrapperType = TextWrapperType.DIV) {

    val content = @Composable {
        org.jetbrains.compose.web.dom.Text(text)
    }
    when (wrapperType) {
        TextWrapperType.DIV -> Div(attrs = modifier.asAttributesBuilder()) {
            content()
        }

        TextWrapperType.SPAN -> Span(attrs = modifier.asAttributesBuilder()) {
            content()
        }

        TextWrapperType.P -> P(attrs = modifier.asAttributesBuilder()) {
            content()
        }
    }

}

enum class TextWrapperType {
    DIV,
    SPAN,
    P,
}