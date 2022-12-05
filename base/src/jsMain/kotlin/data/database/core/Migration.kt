package data.database.core

data class Migration(val prev: Set<Int>, val migrate: suspend MigrationScope.() -> Unit)

interface MigrationScope {
}