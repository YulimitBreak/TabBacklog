package entity

interface MultiBookmarkSource {

    class Url(val urls: Set<String>) : MultiBookmarkSource
}