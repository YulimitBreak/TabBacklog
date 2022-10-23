import com.juul.indexeddb.Database
import com.juul.indexeddb.openDatabase
import data.database.core.DatabaseHolder
import data.database.core.DbSchema
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class TestDatabaseHolder(
    private val databaseName: String,
    private val schema: List<DbSchema<*>>
) : DatabaseHolder {

    private var database: Database? = null

    private var databaseOpeningJob: Job? = null

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

    private suspend fun openDatabaseInstance(): Database {
        deleteDatabase()
        return openDatabase(databaseName, 1) { database, oldVersion, newVersion ->
            if (oldVersion < 1) {
                schema.forEach { schema ->
                    with(schema) {
                        createObjectStore(database)
                    }
                }
            }
        }
    }

    suspend fun deleteDatabase() {
        database?.close()
        try {
            com.juul.indexeddb.deleteDatabase(databaseName)
        } catch (_: Exception) {
        }
    }
}