package ui.page.summary

import entity.Bookmark

fun interface BookmarkUpdateListener {
    fun onUpdate(newBookmark: Bookmark)
}