package ui.page.editor

enum class BookmarkEditedBlock {
    TITLE,
    COMMENT,
    TAGS,
    REMINDER,
    DEADLINE,
    EXPIRATION,
}

enum class TimerType(val block: BookmarkEditedBlock) {
    REMINDER(BookmarkEditedBlock.REMINDER),
    DEADLINE(BookmarkEditedBlock.DEADLINE),
    EXPIRATION(BookmarkEditedBlock.EXPIRATION),
}