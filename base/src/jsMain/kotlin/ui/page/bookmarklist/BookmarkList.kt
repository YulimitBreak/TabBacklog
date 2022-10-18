package ui.page.bookmarklist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import di.ModuleLocal

@Composable
fun BookmarkList(modifier: Modifier = Modifier) {

    val appModule = ModuleLocal.App.current
    val scope = rememberCoroutineScope()
    val model = remember() {
        appModule.createBookmarkListModel(scope)
    }

    Column(modifier) {

    }
}