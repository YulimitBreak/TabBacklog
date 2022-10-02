package data.database.core

import com.juul.indexeddb.*

interface DbField {

    val storeName: String

    val index: Index?

    val name: String

    sealed class Index {
        object PrimaryKey : Index()
        object Autoincrement : Index()
        data class Field(val unique: Boolean = false) : Index()
        data class Composite(val reference: List<DbField>, val unique: Boolean = false) : Index() {
            constructor(vararg reference: DbField, unique: Boolean = false) : this(reference.toList(), unique)
        }
    }
}

interface EntityDbField<T> : DbField {
    val backingField: ((T) -> dynamic)?
}

value class DbSchema<Field : DbField>(val fields: List<Field>) {

    val storeName: String get() = fields.first().storeName

    fun VersionChangeTransaction.createObjectStore(database: Database) {
        val autoincrement = fields.map { it.index }.filterIsInstance<DbField.Index.Autoincrement>().singleOrNull()

        console.log("Creating object store $storeName")
        val store: ObjectStore = if (autoincrement != null) {
            database.createObjectStore(storeName, AutoIncrement)
        } else {
            val primaryKeys = fields.filter { it.index is DbField.Index.PrimaryKey }
            val firstKey = primaryKeys.first().name
            val otherKeys = primaryKeys.drop(1).map { it.name }.toTypedArray()
            database.createObjectStore(storeName, KeyPath(firstKey, *otherKeys))
        }

        fields
            .mapNotNull { Pair(it.name, it.index as? DbField.Index.Field ?: return@mapNotNull null) }
            .forEach { (name, index) ->
                console.log("Creating index for field $name")
                store.createIndex(name, KeyPath(name), unique = index.unique)
            }

        fields
            .mapNotNull { Pair(it.name, it.index as? DbField.Index.Composite ?: return@mapNotNull null) }
            .forEach { (name, index) ->
                val firstKey = index.reference.first().name
                val otherKeys = index.reference.drop(1).map { it.name }.toTypedArray()
                console.log("Creating composite index $name")
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