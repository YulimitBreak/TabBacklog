package ui.manager

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.RowScope
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.toCssColor
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.silk.components.icons.fa.FaBookOpen
import com.varabyte.kobweb.silk.components.icons.fa.FaLayerGroup
import com.varabyte.kobweb.silk.components.icons.fa.FaRobot
import com.varabyte.kobweb.silk.components.icons.fa.FaTags
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.px
import ui.common.basecomponent.RowButton
import ui.styles.Palette
import ui.styles.Variants

@Composable
fun ManagerNavigator(
    openedPage: ManagerNavigationPage,
    modifier: Modifier = Modifier,
    onPageRequest: (ManagerNavigationPage) -> Unit,
) {

    Row(
        modifier.role("navigation").borderBottom(4.px, LineStyle.Dashed, Palette.primaryColor.toCssColor())
            .padding(topBottom = 16.px),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {

        @Composable
        fun PageButton(
            page: ManagerNavigationPage,
            modifier: Modifier = Modifier,
            content: @Composable RowScope.() -> Unit
        ) {
            RowButton(
                onClick = { onPageRequest(page) },
                modifier.padding(leftRight = 16.px, topBottom = 8.px).textAlign(TextAlign.Center).fontSize(1.4.em)
                    .margin(left = 32.px)
                    .thenIf(
                        page == openedPage,
                        Variants.Button.SelectedUnclickablePrimary.toModifier().fontWeight(FontWeight.Normal)
                    ),
                content
            )
        }

        PageButton(ManagerNavigationPage.COLLECTION) {
            FaBookOpen()
            SpanText("Collection")
        }

        PageButton(ManagerNavigationPage.TABS) {
            FaLayerGroup()
            SpanText("Tabs")
        }

        PageButton(ManagerNavigationPage.TAG_LIST) {
            FaTags()
            SpanText("Tag list")
        }

        PageButton(ManagerNavigationPage.AUTO_TAG) {
            FaRobot()
            SpanText("Auto-tagging")
        }
    }
}