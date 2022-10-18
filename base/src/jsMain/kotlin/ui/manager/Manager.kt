package ui.manager

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.height
import org.jetbrains.compose.web.css.px

@Composable
fun Manager() {

    Column(modifier = Modifier.fillMaxSize()) {

        ManagerNavigator(
            ManagerNavigationPage.COLLECTION,
            modifier = Modifier.fillMaxWidth().height(64.px)
        ) {

        }
    }
}