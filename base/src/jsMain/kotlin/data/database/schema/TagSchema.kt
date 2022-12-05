package data.database.schema

import data.database.core.DbField

enum class TagSchema(override val index: DbField.Index) : DbField {
    Url(DbField.Index.PrimaryKey(indexed = true)),
    Tag(DbField.Index.PrimaryKey(indexed = true)),
    ;

    override val storeName: String = "tags"
}