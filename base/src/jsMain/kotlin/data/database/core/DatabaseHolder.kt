package data.database.core

import com.juul.indexeddb.Database

interface DatabaseHolder {
    suspend fun database(): Database
}