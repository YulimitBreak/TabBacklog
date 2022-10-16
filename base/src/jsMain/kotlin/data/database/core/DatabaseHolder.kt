package data.database.core

import com.juul.indexeddb.Database
import com.juul.indexeddb.VersionChangeTransaction
import com.juul.indexeddb.deleteDatabase
import com.juul.indexeddb.openDatabase
import data.database.schema.BookmarkSchema
import data.database.schema.TagSchema

class DatabaseHolder {

    companion object {
        // Only for development
        const val FORCE_RECREATE: Boolean = true

        const val DATABASE_NAME = "TabBacklog"
        const val Version = 1
        val schema = listOf(
            DbSchema<BookmarkSchema>(),
            DbSchema<TagSchema>()
        )
    }

    private var database: Database? = null

    suspend fun database() = database
        ?: openDatabaseInstance().also { database = it }


    private suspend fun openDatabaseInstance(): Database {
        console.log("Opening database")
        suspend fun open() = openDatabase(DATABASE_NAME, Version) { database, oldVersion, newVersion ->
            migrate(database, oldVersion, newVersion)
        }
        return try {
            open()
        } catch (e: Exception) {
            console.error("Open database error")
            e.printStackTrace()
            try {
                deleteDatabase(DATABASE_NAME)
            } catch (e: Exception) {
                console.error("Delete database error")
                e.printStackTrace()
            }
            open()
        }
    }

    private fun VersionChangeTransaction.migrate(database: Database, oldVersion: Int, newVersion: Int) {
        console.log("Migrate $oldVersion -> $newVersion")
        if (FORCE_RECREATE && newVersion != oldVersion && oldVersion != 0) {
            console.log("ForceRecreate caused destructive migration")
            migrateDestructively(database)
        } else if (oldVersion == 0 && newVersion != 0) {
            console.log("Database not found, initializing")
            initialize(database)
        } else {
            for (versionFrom in oldVersion until newVersion) {
                console.log("Migrating from $versionFrom to $versionFrom")
                val success = upgradeTo(versionFrom + 1, database)
                if (!success) {
                    console.log("Failure to migrate")
                    migrateDestructively(database)
                    return
                }
            }
        }
    }

    private fun VersionChangeTransaction.migrateDestructively(database: Database) {
        console.log("Starting destructive migration")
        schema.forEach {
            // FIXME doesn't work if store doesn't exist
            console.log("Deleting object store ${it.storeName}")
            database.deleteObjectStore(it.storeName)
        }
        initialize(database)
    }

    private fun VersionChangeTransaction.initialize(database: Database) {
        console.log("Starting initialization")
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