package data.database.schema

import data.database.core.DatabaseVersion
import data.database.core.IndexSchema
import data.database.core.Migration

enum class VersionHistory(
    override val versionNumber: Int,
    override val schema: Set<IndexSchema>,
    override val migrations: Set<Migration> = emptySet()
) : DatabaseVersion {

    V1(
        1,
        setOf(
            IndexSchema("bookmarks", "Title", "Type", "CreationDate", "RemindDate", "Deadline", "ExpirationDate"),
            IndexSchema("tags", "Url", "Tag"),
            IndexSchema("tag_count", "Tag"),
        ),
    ),
    V2(2, V1.schema)


    ;

    companion object {
        init {
            require(VersionHistory.values().distinctBy { it.versionNumber }.size == VersionHistory.values().size)
        }
    }
}