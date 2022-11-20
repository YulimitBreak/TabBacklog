package data

import com.juul.indexeddb.bound
import data.database.core.DatabaseHolder
import data.database.core.DbSchema
import data.database.schema.TagCountSchema

class TagRepository(private val databaseHolder: DatabaseHolder) {

    suspend fun fetchTagAutocomplete(tag: String): List<String> {
        if (tag.isBlank()) return emptyList()
        val schema = DbSchema<TagCountSchema>()
        return databaseHolder.database().transaction(schema.storeName) {
            val tags = objectStore(schema.storeName)
                .getAll(bound(tag, tag + '\uffff'))
                .map { schema.extract(it) { TagCountSchema.Tag.value<String>() to TagCountSchema.Count.value<Int>() } }
            tags.sortedByDescending { it.second }
                .take(6)
                .map { it.first }
        }
    }
}