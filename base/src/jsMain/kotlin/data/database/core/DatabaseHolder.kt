package data.database.core

import com.juul.indexeddb.Database
import com.juul.indexeddb.VersionChangeTransaction
import com.juul.indexeddb.openDatabase
import data.database.schema.BookmarkSchema
import data.database.schema.TagSchema

class DatabaseHolder {

    companion object {
        // Only for development
        const val FORCE_RECREATE: Boolean = true

        const val Version = 1
        val schema = listOf(
            DbSchema<BookmarkSchema>(),
            DbSchema<TagSchema>()
        )
    }

    private var database: Database? = null

    suspend fun database() = database
        ?: openDatabaseInstance().also { database = it }


    private suspend fun openDatabaseInstance(): Database =
        openDatabase("TabBacklog", Version) { database, oldVersion, newVersion ->
            if (FORCE_RECREATE) {
                migrateDestructively(database)
            } else {
                migrate(database, oldVersion, newVersion)
            }
        }

    private fun VersionChangeTransaction.migrate(database: Database, oldVersion: Int, newVersion: Int) {
        if (oldVersion == 0 && newVersion != 0) {
            initialize(database)
        } else {
            for (versionFrom in oldVersion until newVersion) {
                val success = upgradeTo(versionFrom + 1, database)
                if (!success) {
                    migrateDestructively(database)
                    return
                }
            }
        }
    }

    private fun VersionChangeTransaction.migrateDestructively(database: Database) {
        schema.forEach {
            database.deleteObjectStore(it.storeName)
        }
        initialize(database)
    }

    private fun VersionChangeTransaction.initialize(database: Database) {
        schema.forEach { schema ->
            with(schema) {
                createObjectStore(database)
            }
        }
    }

    // TODO better versioning system for 1.0
    private fun VersionChangeTransaction.upgradeTo(newVersion: Int, database: Database): Boolean {
        return false
    }
}