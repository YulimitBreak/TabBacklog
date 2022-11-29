package ui.page.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import com.varabyte.kobweb.compose.ui.Modifier
import di.AppModule
import entity.MultiBookmarkSource

@Composable
fun BookmarkMultiEditor(
    target: MultiBookmarkSource,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appModule = AppModule.Local.current
    val scope = rememberCoroutineScope()
    val onNavigateBackState = rememberUpdatedState(BookmarkEditorModel.OnNavigateBack(onNavigateBack))
    val model = remember(target) {
        appModule.createBookmarkMultiEditorModel(scope, target, onNavigateBackState)
    }
}