package ui.page.tagedit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import data.TagRepository
import kotlinx.coroutines.CoroutineScope

class TagEditModel(
    private val scope: CoroutineScope,
    private val tagRepository: TagRepository,
) {

    var selectedTag: String? by mutableStateOf(null)
        private set

    var editedTag: String by mutableStateOf("")
        private set

    private var storedEditedTag: String = ""

    fun selectTag(tag: String) {
        if (selectedTag == null) {
            storedEditedTag = editedTag
        }
        editedTag = tag
        selectedTag = tag
    }

    fun deselectTag() {
        selectedTag = null
        editedTag = storedEditedTag
    }
}