package ui.page.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.icons.fa.FaArrowLeft
import com.varabyte.kobweb.silk.components.icons.fa.FaStar
import com.varabyte.kobweb.silk.components.icons.fa.FaTrash
import com.varabyte.kobweb.silk.components.icons.fa.IconStyle
import com.varabyte.kobweb.silk.components.style.toModifier
import di.ModuleLocal
import entity.Url
import org.jetbrains.compose.web.css.minus
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Text
import ui.common.basecomponent.LoadableView
import ui.common.basecomponent.RowButton
import ui.common.bookmark.BookmarkTitleEdit
import ui.common.bookmark.BookmarkTitleView
import ui.common.styles.components.BookmarkEditClickableArea

@Composable
fun BookmarkEditor(
    url: String?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appModule = ModuleLocal.App.current
    val scope = rememberCoroutineScope()
    val model = remember(url) {
        appModule.createBookmarkEditorModel(scope, url?.let(::Url))
    }

    LoadableView(model.bookmark, modifier = modifier.minHeight(300.px)) { bookmark, m ->
        Column(m.gap(8.px)) {
            Row(Modifier.fillMaxWidth().gap(8.px)) {

                RowButton(onClick = onNavigateBack) {
                    FaArrowLeft()
                    Text("Back")
                }

                Spacer()
                RowButton(
                    onClick = { model.updateFavorite(!bookmark.favorite) },
                ) {
                    FaStar(style = if (bookmark.favorite) IconStyle.FILLED else IconStyle.OUTLINE)
                    Text("Favorite")
                }
                if (!bookmark.isNew) {
                    RowButton(onClick = { model.deleteBookmark(onNavigateBack) }) {
                        FaTrash()
                        Text("Delete")
                    }
                }
            }

            if (model.editedBlock != EditedBlock.TITLE) {
                BookmarkTitleView(bookmark.title, bookmark.base.favicon, bookmark.base.url,
                    BookmarkEditClickableArea.Style.toModifier()
                        .width(100.percent - 16.px)
                        .height(64.px)
                        .margin(leftRight = 4.px, topBottom = (-2).px) // Negative margin to compensate for border
                        .padding(leftRight = 2.px)
                        .onClick {
                            model.requestEdit(EditedBlock.TITLE)
                        }
                )
            } else {
                BookmarkTitleEdit(
                    bookmark.title, bookmark.base.favicon,
                    onInput = { model.updateTitle(it) },
                    Modifier.margin(leftRight = 8.px)
                        .height(64.px)
                        .width(100.percent - 16.px)
                        .onKeyDown { event ->
                            if (event.getNormalizedKey() == "Enter") {
                                event.preventDefault()
                                model.requestEdit(null)
                            }
                        }
                )
            }
        }
    }
}