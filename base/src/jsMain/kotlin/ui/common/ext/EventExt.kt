package ui.common.ext

import ui.page.tagedit.TagEditEvent

fun TagEditEvent.apply(tags: List<String>) = when (this) {
    is TagEditEvent.Add -> {
        if (tags.contains(this.tag)) {
            tags
        } else {
            tags + this.tag
        }
    }

    is TagEditEvent.Delete -> {
        tags - this.tag
    }

    is TagEditEvent.Edit -> {
        if (tags.contains(this.to)) {
            tags - this.from
        } else {
            tags - this.from + this.to
        }
    }
}