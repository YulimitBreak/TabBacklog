package ui.page.collection

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.role
import ui.common.basecomponent.DivText

@Composable
fun CollectionView(modifier: Modifier = Modifier) {

    Column(modifier.role("main")) {
        DivText("Body TODO")
    }
}