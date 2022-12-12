package data.database.schema

import data.database.core.MigrationManager

class AppMigrationManager : MigrationManager() {
    override val migrations: Map<Int, Migration> = mapOf(

    )
}