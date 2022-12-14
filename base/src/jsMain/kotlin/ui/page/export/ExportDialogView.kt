package ui.page.export

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexWrap
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.justifyContent
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.silk.components.icons.fa.FaFileExport
import com.varabyte.kobweb.silk.components.icons.fa.FaFileImport
import com.varabyte.kobweb.silk.components.icons.fa.FaXmark
import di.AppModule
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.px
import ui.common.basecomponent.BlobFileInput
import ui.common.basecomponent.DivText
import ui.common.basecomponent.LoadingSpinner
import ui.common.basecomponent.RowButton

@Composable
fun ExportDialogView(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
) {
    val appModule = AppModule.Local.current
    val scope = rememberCoroutineScope()
    val onDismissState = rememberUpdatedState(ExportDialogModel.OnDismiss(onDismiss))
    val model: ExportDialogModel = remember() {
        appModule.createExportDialogModel(scope, onDismissState)
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (model.isLoading) {
            LoadingSpinner()
        } else {
            Row(
                modifier = Modifier.fillMaxWidth().justifyContent(JustifyContent.SpaceBetween)
                    .flexWrap(FlexWrap.Nowrap).gap(32.px),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RowButton(onClick = { model.exportBookmarks() }) {
                    FaFileExport()
                    DivText("Export")
                }
                BlobFileInput("import_file_input", "application/json",
                    onFileInput = { model.importBookmarks(it) }
                ) { onClick ->
                    RowButton(onClick = onClick) {
                        FaFileImport()
                        DivText("Import")
                    }
                }
                FaXmark(modifier = Modifier.fontSize(1.5.em).onClick { onDismiss() })
            }
        }
    }
}