package data.database.core

interface IndexSchema {
    val storeName: String
    val indices: Set<String>

    companion object {
        operator fun invoke(storeName: String, vararg indices: String) = object : IndexSchema {
            override val storeName: String = storeName
            override val indices: Set<String> = indices.toSet()
        }
    }
}