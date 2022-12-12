package data.database.core

import com.juul.indexeddb.VersionChangeTransaction

// Always assume that migration happens from previous version, YAGNI anything more complex
data class Migration(
    val requiresIndexUpdate: Boolean = false,
    val migrate: suspend MigrationScope.() -> Unit
)

interface MigrationScope {

    suspend fun transaction(action: suspend VersionChangeTransaction.() -> Unit)

    suspend fun update(store: String, criteria: MigrationUpdateScope.() -> Unit)
}

interface MigrationUpdateScope {
    val source: Map<String, dynamic>
    val destination: MutableMap<String, dynamic>

    fun deleteItem()

    fun <T> String.value() = source[this] as T

    fun <T> String.save(value: T) {
        destination[this] = value
    }

    fun <T> String.updateValue(update: (T) -> T) = save(update(value()))

    fun String.delete() {
        destination.remove(this)
    }
}