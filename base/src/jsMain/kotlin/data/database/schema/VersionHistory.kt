package data.database.schema

import data.database.core.DatabaseVersion
import data.database.core.IndexSchema
import data.database.core.Migration

enum class VersionHistory(
    override val versionNumber: Int,
    override val schema: Set<IndexSchema>,
    override val migration: Migration? = null
) : DatabaseVersion {

    V1(
        1,
        setOf(
            IndexSchema(
                "bookmarks",
                setOf("Url"),
                "Title",
                "Type",
                "CreationDate",
                "RemindDate",
                "Deadline",
                "ExpirationDate"
            ),
            IndexSchema("tags", setOf("Url, Tag"), "Url", "Tag"),
            IndexSchema("tag_count", setOf(), "Tag"),
        ),
    ),
    V2(2, V1.schema)


    ;

    companion object {
        init {
            require(values().distinctBy { it.versionNumber }.size == values().size)
        }
    }
}