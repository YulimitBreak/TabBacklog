package data

import com.juul.indexeddb.bound
import data.database.core.DatabaseHolder
import data.database.core.DbSchema
import data.database.schema.TagSchema

class TagRepository(private val databaseHolder: DatabaseHolder) {

    suspend fun fetchTagAutocomplete(tag: String): List<String> {
        if (tag.isBlank()) return emptyList()
        val schema = DbSchema<TagSchema>()
        return databaseHolder.database().transaction(schema.storeName) {
            val tags = objectStore(schema.storeName).index(TagSchema.Tag.name)
                .getAll(bound(tag, tag + '\uffff'))
                .map { schema.extract(it) { TagSchema.Tag.value<String>() } }
            tags.groupBy { it }
                .mapValues { it.value.size }.toList()
                .sortedByDescending { it.second }
                .take(6)
                .map { it.first }
        }
    }
}