package data.database.core

import common.generateHashcode

interface IndexSchema {
    val storeName: String
    val indices: Set<String>

    companion object {
        operator fun invoke(storeName: String, vararg indices: String) = object : IndexSchema {
            override val storeName: String = storeName
            override val indices: Set<String> = indices.toSet()

            override fun equals(other: Any?): Boolean {
                if (other !is IndexSchema) return false
                return this.storeName == other.storeName && this.indices == other.indices
            }

            override fun hashCode(): Int =
                generateHashcode(this.storeName, this.indices)


            override fun toString(): String = "INDEX $storeName: $indices"
        }
    }
}