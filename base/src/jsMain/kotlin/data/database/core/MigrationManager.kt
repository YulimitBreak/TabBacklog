package data.database.core

import com.juul.indexeddb.Database
import com.juul.indexeddb.VersionChangeTransaction

class MigrationManager(private val versionHistory: List<DatabaseVersion>) {

    suspend fun VersionChangeTransaction.migrate(database: Database, oldVersion: Int, newVersion: Int) {
        val migrationPathList = findMigrationPath(oldVersion, newVersion)
        migrationPathList.forEach { path ->

        }
    }

    private fun findMigrationPath(from: Int, to: Int): List<MigrationPath> {
        val versions = versionHistory.dropWhile { it.versionNumber < from }.dropLastWhile { it.versionNumber > to }
        if (versions.isEmpty()) return emptyList()
        val migrations = versions.drop(1).mapNotNull { version -> version.migration?.let { Pair(version, it) } }
        if (migrations.isEmpty()) return listOf(
            MigrationPath(
                versions.first().schema, versions.last().schema, emptyList()
            )
        )
        if (migrations.all { !it.second.requiresIndexUpdate }) return listOf(
            MigrationPath(
                versions.first().schema, versions.last().schema, migrations.map { it.second }
            )
        )
        // Only happens when there are migrations requiring index update
        throw NotImplementedError("Hope I will not need it")
    }

    private data class MigrationPath(
        val startIndex: Set<IndexSchema>,
        val endIndex: Set<IndexSchema>,
        val migrations: List<Migration>
    )
}