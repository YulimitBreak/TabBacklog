package data.database.core

import com.juul.indexeddb.Database
import com.juul.indexeddb.VersionChangeTransaction

abstract class MigrationManager {
    abstract val migrations: Map<Int, Migration>

    suspend fun migrate(transaction: VersionChangeTransaction, database: Database, oldVersion: Int, newVersion: Int) {
        val migrations = this@MigrationManager.migrations
            .filterKeys { it in (oldVersion + 1)..newVersion }
            .toList()
            .sortedBy { it.first }
            .map { it.second }
        migrations.forEach { it.migrate(transaction, database) }
    }

    fun interface Migration {
        suspend fun migrate(transaction: VersionChangeTransaction, database: Database)
    }
}