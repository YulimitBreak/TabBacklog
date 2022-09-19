package ui.common

import androidx.compose.runtime.Composable
import entity.EditedBookmark
import entity.Loadable
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text

@Composable
fun BookmarkContent(bookmark: Loadable<EditedBookmark>, onBookmarkChange: (EditedBookmark) -> Unit) {
    when (bookmark) {
        is Loadable.Error -> BookmarkError(bookmark.error)
        is Loadable.Loading -> BookmarkLoading()
        is Loadable.Success -> BookmarkEditor(bookmark.value, onBookmarkChange)
    }
}

@Composable
fun BookmarkError(error: Throwable) {
    Div(attrs = {
        style {
            display(DisplayStyle.Flex)
            alignItems(AlignItems.Center)
            flexDirection(FlexDirection.Column)
            height(100.percent)
            width(100.percent)
            alignContent(AlignContent.Center)
            justifyContent(JustifyContent.Center)
        }
    }) {
        P { Text("Extension has encountered an error") }

        error.message?.let {
            P(attrs = { style { color(Color.red) } }) {
                Text(it)
            }
        }
    }
}

@Composable
fun BookmarkLoading() {
    Div(attrs = {
        style {
            width(100.percent)
            height(100.percent)
            display(DisplayStyle.Flex)
            alignItems(AlignItems.Center)
            justifyContent(JustifyContent.Center)
        }
    }) {
        Loader()
    }
}