package data.database.schema

import data.database.core.DbField

enum class TagCountSchema(override val index: DbField.Index? = null) : DbField {
    Tag(DbField.Index.PrimaryKey()),
    Count,
    ;

    override val storeName: String = "tag_count"
}