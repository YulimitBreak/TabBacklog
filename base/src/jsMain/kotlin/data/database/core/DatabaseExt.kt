package data.database.core

import com.juul.indexeddb.CursorStart
import com.juul.indexeddb.Database
import com.juul.indexeddb.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList

fun <T> Database.paginate(
    vararg stores: String,
    pageSize: Int = 10,
    cursorProvider: suspend Transaction.(advance: CursorStart.Advance?) -> Flow<T>
): Flow<T> = flow {
    var offset = 0
    while (true) {
        val list = transaction(*stores) {
            cursorProvider(CursorStart.Advance(offset).takeIf { offset > 0 }).take(pageSize).toList()
        }
        list.forEach { emit(it) }
        if (list.size < pageSize) break
        offset += list.size
    }
}