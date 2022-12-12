package data.database.core

import com.juul.indexeddb.Database
import com.juul.indexeddb.VersionChangeTransaction
import com.juul.indexeddb.deleteDatabase
import com.juul.indexeddb.openDatabase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class AppDatabaseHolder(
    private val databaseName: String,
    private val version: Int,
    private val schema: List<DbSchema<*>>,
    private val migrationManager: MigrationManager,
) : DatabaseHolder {

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
            if (e is CancellationException) throw e
            console.error("Open database error")
            e.printStackTrace()
            try {
                deleteDatabase(databaseName)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                console.error("Delete database error")
                e.printStackTrace()
            }
            open()
        }
    }

    private suspend fun VersionChangeTransaction.migrate(database: Database, oldVersion: Int, newVersion: Int) {
        console.log("Migrate $oldVersion -> $newVersion")
        if (oldVersion == 0 && newVersion != 0) {
            initialize(database)
        } else {
            migrationManager.migrate(this, database, oldVersion, newVersion)
        }
    }

    private fun VersionChangeTransaction.initialize(database: Database) {
        console.log("Starting initialization")
        schema.forEach { schema ->
            with(schema) {
                createObjectStore(database)
            }
        }
    }
}