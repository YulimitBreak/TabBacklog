package ui.page.tablist

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.UserSelect
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.toCssColor
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.silk.components.text.SpanText
import di.AppModule
import entity.BrowserTab
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.minus
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Text
import ui.common.basecomponent.LoadableView
import ui.common.basecomponent.LoadingTable
import ui.common.basecomponent.RowButton
import ui.common.basecomponent.Toggle
import ui.styles.Palette
import ui.styles.Variants
import ui.styles.primaryColors

@Composable
fun TabListView(
    modifier: Modifier = Modifier,
    onTabSelect: (urls: Set<BrowserTab>) -> Unit
) {
    val appModule = AppModule.Local.current
    val scope = rememberCoroutineScope()
    val onTabSelectState = rememberUpdatedState(TabListModel.OnTabSelect(onTabSelect))
    val model: TabListModel = remember() {
        appModule.createTabListModel(scope, onTabSelectState)
    }

    Column(modifier) {
        // TODO sort layout

        LoadableView(
            model.openedWindows,
            Modifier.padding(leftRight = 16.px, topBottom = 8.px).width(100.percent - 32.px)
        ) { windows, m ->
            Row(m.gap(8.px)) {
                windows.forEachIndexed { index, windowId ->
                    key(windowId) {
                        RowButton(
                            onClick = { model.selectWindow(windowId) },
                            modifier = Modifier.thenIf(
                                model.selectedWindow == windowId,
                                Variants.Button.SelectedUnclickablePrimary.toModifier()
                            )
                        ) {
                            Text("Window ${index + 1}")
                        }
                    }
                }
            }
        }

        LoadingTable(
            model.tabs,
            model.isLoading,
            Modifier.margin(leftRight = 16.px).width(100.percent - 32.px)
                .minHeight(20.percent)
                .flexGrow(1)
                .border(1.px, LineStyle.Solid, Palette.Local.current.onBackground.toCssColor()),
            onLoadMore = {
                model.requestMore()
            }.takeIf { !model.reachedEnd }
        ) { tab ->
            key(tab.tabId) {
                TabTableItemView(
                    tab,
                    Modifier.padding(topBottom = 4.px, leftRight = 8.px).width(100.percent - 16.px)
                        .thenIf(model.selectedTabs.contains(tab.tabId), Modifier.primaryColors())
                        .userSelect(UserSelect.None)
                        .onClick { event -> model.selectTab(tab, event.ctrlKey, event.shiftKey) }
                )
            }
        }

        Row(modifier = Modifier.width(100.percent - 32.px).padding(leftRight = 16.px, topBottom = 4.px).gap(8.px)) {
            Toggle(model.multiSelectMode, "Multi-select mode", onToggle = { model.toggleMultiSelectMode(it) })
            SpanText("or use Ctrl and Shift keys while selecting")
        }
    }
}