package ui.page.summary

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.varabyte.kobweb.compose.ui.Modifier
import di.ModuleLocal
import entity.Bookmark


@Composable
fun BookmarkSummary(
    bookmark: Bookmark,
    modifier: Modifier = Modifier
) {
    val appModule = ModuleLocal.App.current
    val scope = rememberCoroutineScope()

    val model = remember { appModule.createBookmarkSummaryModel(scope) }
}