package data.database.core

import com.juul.indexeddb.Database
import com.juul.indexeddb.VersionChangeTransaction
import com.juul.indexeddb.deleteDatabase
import com.juul.indexeddb.openDatabase
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class AppDatabaseHolder(
    private val databaseName: String,
    private val version: Int,
    private val schema: List<DbSchema<*>>,
) : DatabaseHolder {

    companion object {
        // Only for development
        const val FORCE_RECREATE: Boolean = true
    }

    private var database: Database? = null

    override tailrec suspend fun database(): Database {
        database?.let { return it }
        if (databaseOpeningJob?.isActive == true) {
            databaseOpeningJob?.join()
            return database()
        }
        coroutineScope {
            databaseOpeningJob = launch {
                openDatabaseInstance().also { database = it }
            }
        }
        return database()
    }

    private var databaseOpeningJob: Job? = null

    private suspend fun openDatabaseInstance(): Database {
        console.log("Opening database")
        suspend fun open() = openDatabase(databaseName, version) { database, oldVersion, newVersion ->
            migrate(database, oldVersion, newVersion)
        }
        return try {
            open()
        } catch (e: Exception) {
            console.error("Open database error")
            e.printStackTrace()
            try {
                deleteDatabase(databaseName)
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