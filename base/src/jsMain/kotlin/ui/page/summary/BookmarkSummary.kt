package ui.page.summary

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.icons.fa.*
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.SilkTheme
import common.DateUtils
import common.styleProperty
import di.ModuleLocal
import entity.Bookmark
import entity.BookmarkType
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Text
import ui.common.basecomponent.DivText
import ui.common.basecomponent.RowButton
import ui.common.basecomponent.TagListView
import ui.common.bookmark.BookmarkTitleView
import ui.common.styles.MainStyle
import ui.common.styles.primaryColors


@Composable
fun BookmarkSummary(
    bookmark: Bookmark,
    onBookmarkUpdate: (Bookmark) -> Unit,
    onEditRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appModule = ModuleLocal.App.current
    val scope = rememberCoroutineScope()

    val model: BookmarkSummaryModel = remember { appModule.createBookmarkSummaryModel(scope) }

    Column(modifier.gap(8.px)) {
        Row(Modifier.fillMaxWidth().gap(8.px)) {

            RowButton(onClick = { model.openManager() }) {
                FaListCheck()
                Text("Open manager")
            }

            Spacer()
            if (bookmark.isSaved) {
                RowButton(
                    onClick = { onBookmarkUpdate(bookmark.copy(favorite = !bookmark.favorite)) },
                ) {
                    FaStar(style = if (bookmark.favorite) IconStyle.FILLED else IconStyle.OUTLINE)
                    Text("Favorite")
                }
                RowButton(onClick = { onBookmarkUpdate(bookmark.copy(creationDate = null)) }) {
                    FaTrash()
                    Text("Delete")
                }
            }
        }
        BookmarkTitleView(bookmark.title, bookmark.favicon, bookmark.url, Modifier.fillMaxWidth())
        Row(Modifier.fillMaxWidth().gap(8.px)) {
            val currentType = bookmark.takeIf { it.isSaved }?.type
            RowButton(
                onClick = {
                    onBookmarkUpdate(bookmark.copy(type = BookmarkType.LIBRARY, creationDate = DateUtils.now))
                }, modifier = if (currentType == BookmarkType.LIBRARY) {
                    SelectedBookmarkTypeModifier
                } else {
                    Modifier
                }.width(30.percent)
            ) {
                FaBookBookmark()
                if (currentType == BookmarkType.LIBRARY) {
                    Text("In library")
                } else {
                    Text("To library")
                }
            }
            RowButton(
                onClick = {
                    onBookmarkUpdate(bookmark.copy(type = BookmarkType.BACKLOG, creationDate = DateUtils.now))
                },
                modifier = if (currentType == BookmarkType.BACKLOG) {
                    SelectedBookmarkTypeModifier
                } else {
                    Modifier
                }.width(30.percent)
            ) {
                FaNoteSticky()
                if (currentType == BookmarkType.BACKLOG) {
                    Text("In backlog")
                } else {
                    Text("To backlog")
                }
            }

            Spacer()

            RowButton(onClick = {
                onEditRequest()
            }) {
                FaPencil()
                Text("Edit")
            }
        }

        if (bookmark.comment.isNotBlank()) {
            SpanText("Comment:")
            DivText(bookmark.comment, Modifier.fontWeight(FontWeight.Lighter).margin(leftRight = 8.px))
        }

        if (bookmark.tags.isNotEmpty()) {
            SpanText("Tags:")
            TagListView(
                bookmark.tags.toList(), Modifier.margin(leftRight = 8.px),
                tagModifier = {
                    Modifier.fontSize(0.8.em).padding(leftRight = 4.px, topBottom = 2.px)
                        .primaryColors()
                        .borderRadius(4.px)
                }
            )
        }
    }
}

private val SelectedBookmarkTypeModifier
    @Composable
    @ReadOnlyComposable
    get() = Modifier
        .styleProperty("pointer-events", "none")
        .backgroundColor(SilkTheme.palette.background)
        .color(MainStyle.primaryColor)
        .fontWeight(FontWeight.Lighter)