package data.database

import com.juul.indexeddb.*
import common.safeCast

interface DbField {

    val index: Index?

    val name: String

    sealed class Index {
        data class PrimaryKey(val storeName: String? = null) : Index()
        data class Autoincrement(val storeName: String) : Index()
        data class Field(val unique: Boolean) : Index()
        data class Composite(val reference: List<DbField>, val unique: Boolean) : Index() {
            constructor(vararg reference: DbField, unique: Boolean = false) : this(reference.toList(), unique)
        }
    }
}

interface EntityDbField<T> : DbField {
    val backingField: ((T) -> dynamic)?
}

value class DbSchema<Field : DbField>(val fields: List<Field>) {
    fun VersionChangeTransaction.createObjectStore(database: Database) {
        val autoincrement = fields.map { it.index }.filterIsInstance<DbField.Index.Autoincrement>().singleOrNull()
        val store: ObjectStore = if (autoincrement != null) {
            database.createObjectStore(autoincrement.storeName, AutoIncrement)
        } else {
            val primaryKeys = fields.filter { it.index is DbField.Index.PrimaryKey }
            val name = primaryKeys
                .mapNotNull { it.index?.safeCast<DbField.Index.PrimaryKey>()?.storeName }
                .distinct()
                .singleOrNull() ?: throw IllegalArgumentException("Undefined store name")
            val firstKey = primaryKeys.first().name
            val otherKeys = primaryKeys.drop(1).map { it.name }.toTypedArray()
            database.createObjectStore(name, KeyPath(firstKey, *otherKeys))
        }

        fields
            .mapNotNull { Pair(it.name, it.index as? DbField.Index.Field ?: return@mapNotNull null) }
            .forEach { (name, index) ->
                store.createIndex(name, KeyPath(name), unique = index.unique)
            }

        fields
            .mapNotNull { Pair(it.name, it.index as? DbField.Index.Composite ?: return@mapNotNull null) }
            .forEach { (name, index) ->
                val firstKey = index.reference.first().name
                val otherKeys = index.reference.drop(1).map { it.name }.toTypedArray()
                store.createIndex(name, KeyPath(firstKey, *otherKeys), unique = index.unique)
            }
    }

    fun <T> extract(source: dynamic, f: (Map<Field, dynamic>) -> T): T {
        return f(
            fields.associateWith { source[it.name] }
        )
    }

    fun generate(map: Map<Field, dynamic>): dynamic {
        val result = js("{}")
        map.forEach { (k, v) -> result[k] = v }
        return result
    }


    companion object {
        inline operator fun <reified E> invoke() where E : DbField, E : Enum<E> =
            DbSchema(enumValues<E>().asList())
    }
}

fun <Field : EntityDbField<T>, T> DbSchema<Field>.generate(item: T): dynamic =
    fields
        .mapNotNull { Pair(it, it.backingField ?: return@mapNotNull null) }
        .toMap()
        .mapValues { (_, backingField) -> backingField(item) }
        .let { generate(it) }