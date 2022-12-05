package data.database.core

interface DatabaseVersion {
    val versionNumber: Int
    val schema: Set<IndexSchema>
    val migrations: Set<Migration>
}