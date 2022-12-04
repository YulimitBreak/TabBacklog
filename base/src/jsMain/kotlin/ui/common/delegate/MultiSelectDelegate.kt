package ui.common.delegate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.math.max
import kotlin.math.min

class MultiSelectDelegate<Data, Id : Any>(private val idGetter: (Data) -> Id) {


    var multiSelectMode: Boolean by mutableStateOf(false)

    var selectedIds: Set<Id> by mutableStateOf(emptySet())
        private set

    var selectedItems: Set<Data> by mutableStateOf(emptySet())
        private set

    private var lastClickedId: Id? = null


    fun selectItem(
        source: List<Data>,
        selectedItem: Data,
        ctrlKey: Boolean,
        shiftKey: Boolean
    ) {
        val id = idGetter(selectedItem)
        val lastClickedId = this.lastClickedId
        when {
            shiftKey && lastClickedId != null && source.any { idGetter(it) == lastClickedId } -> {
                val indexStart = source.indexOfFirst { idGetter(it) == lastClickedId }
                val indexEnd = source.indexOfFirst { idGetter(it) == id }
                if (indexStart == -1 || indexEnd == -1)
                    selectItem(source = source, selectedItem = selectedItem, ctrlKey = true, shiftKey = false)
                this.selectedItems = source.subList(min(indexStart, indexEnd), max(indexStart, indexEnd) + 1)
                    .toSet()
                this.selectedIds = this.selectedItems.map(idGetter).toSet()
            }

            shiftKey || ctrlKey || multiSelectMode -> {
                if (this.selectedIds.contains(id)) {
                    this.selectedIds -= id
                    this.selectedItems = this.selectedItems.filterNot { idGetter(it) == id }.toSet()
                } else {
                    this.selectedIds += id
                    this.selectedItems += selectedItem
                    this.lastClickedId = id
                }
            }

            else -> {
                this.selectedItems = setOf(selectedItem)
                this.selectedIds = setOf(id)
                this.lastClickedId = id
            }
        }
    }
}