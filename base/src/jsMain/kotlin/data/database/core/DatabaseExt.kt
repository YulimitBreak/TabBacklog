package data.database.core

import com.juul.indexeddb.Cursor
import com.juul.indexeddb.CursorStart
import com.juul.indexeddb.CursorWithValue
import com.juul.indexeddb.Database
import com.juul.indexeddb.Key
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList

fun <T> Database.paginate(
    store: String,
    indexName: String? = null,
    query: Key? = null,
    cursorDirection: Cursor.Direction = Cursor.Direction.Next,
    pageSize: Int = 10,
    extractor: (CursorWithValue) -> T
): Flow<T> = flow {
    var offset = 0
    while (true) {
        val list = transaction(store) {
            objectStore(store).let {
                if (indexName != null) it.index(indexName) else it
            }
                .openCursor(
                    query,
                    cursorDirection,
                    if (offset > 0) CursorStart.Advance(offset) else null,
                    autoContinue = true
                )
                .map { extractor(it) }
                .take(pageSize)
                .toList()
        }
        list.forEach { emit(it) }
        if (list.size < pageSize) break
        offset += list.size
    }
}