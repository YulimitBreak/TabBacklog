package entity

sealed interface MultiBookmarkSource {

    class Url(val urls: Set<String>) : MultiBookmarkSource
}