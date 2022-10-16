package ui.page.tagedit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import data.TagRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TagEditModel(
    private val scope: CoroutineScope,
    private val tagRepository: TagRepository,
) {

    var selectedTag: String? by mutableStateOf(null)
        private set

    private val _editedTag = mutableStateOf("")
    var editedTag: String
        get() = _editedTag.value
        private set(value) {
            _editedTag.value = value.take(25).lowercase()
            fetchTagAutocomplete(value)
        }

    var suggestedTags: List<String> by mutableStateOf(emptyList())
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

    fun deleteSelectedTag(onTagEditEvent: (TagEditEvent) -> Unit) {
        onTagEditEvent(TagEditEvent.Delete(selectedTag ?: return))
        deselectTag()
    }

    fun onTagInput(input: String) {
        editedTag = input
    }

    fun confirmTag(onTagEditEvent: (TagEditEvent) -> Unit) {
        val editedTag = editedTag
        val selectedTag = selectedTag
        if (editedTag.isBlank()) {
            if (selectedTag != null) {
                onTagEditEvent(TagEditEvent.Delete(selectedTag))
                deselectTag()
            } else {
                this.editedTag = ""
            }
        } else {
            if (selectedTag != null) {
                if (selectedTag != editedTag) {
                    onTagEditEvent(TagEditEvent.Edit(selectedTag, editedTag))
                }
                deselectTag()
            } else {
                onTagEditEvent(TagEditEvent.Add(editedTag))
                this.editedTag = ""
            }
        }
    }

    private var autocompleteFetchJob: Job? = null

    private fun fetchTagAutocomplete(input: String) {
        autocompleteFetchJob?.cancel()
        autocompleteFetchJob = scope.launch {
            val tags = tagRepository.fetchTagAutocomplete(input)
            if (!isActive) return@launch
            suggestedTags = tags
        }
    }
}

sealed class TagEditEvent {
    data class Add(val tag: String) : TagEditEvent()
    data class Edit(val from: String, val to: String) : TagEditEvent()
    data class Delete(val tag: String) : TagEditEvent()
}