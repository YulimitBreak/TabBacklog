package data.database.core

import com.juul.indexeddb.*
import kotlinx.coroutines.flow.*

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
                .openCursor(query, cursorDirection, if (offset > 0) CursorStart.Advance(offset) else null)
                .map { extractor(it) }
                .take(pageSize)
                .toList()
        }
        list.forEach { emit(it) }
        if (list.size < pageSize) break
        offset += list.size
    }
}