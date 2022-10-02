package data.database.schema

import data.database.core.DbField

enum class TagSchema(override val index: DbField.Index) : DbField {
    Url(DbField.Index.PrimaryKey),
    Tag(DbField.Index.PrimaryKey),
    ;

    override val storeName: String = "tags"
}