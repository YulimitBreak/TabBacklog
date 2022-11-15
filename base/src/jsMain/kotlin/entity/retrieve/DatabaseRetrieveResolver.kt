package entity.retrieve

import com.juul.indexeddb.*
import common.chunkedBy
import common.listTransform
import data.database.core.paginate
import entity.retrieve.util.selectByField
import kotlinx.coroutines.flow.*

abstract class DatabaseRetrieveResolver<T, Query : RetrieveQuery<T>>(
    val database: suspend () -> Database,
) :
    RetrieveResolver<T, Query>() {

    protected abstract val storeName: String
    protected open val paginationPageSize: Int = 10

    protected abstract fun extract(source: dynamic): T

    protected abstract suspend fun resolveQuery(query: Query): DatabaseQuery<T>

    override suspend fun fetchFlow(query: Query?, hasReorderingActions: Boolean): Flow<T> =
        if (!hasReorderingActions) {
            val dbQuery = query?.let { resolveQuery(it) }
            database().paginate(
                storeName, dbQuery?.indexName, dbQuery?.key,
                if (dbQuery?.reverse == true) Cursor.Direction.Previous else Cursor.Direction.Next,
                paginationPageSize
            ) {
                extract(it.value)
            }
                .let { flow ->
                    if (dbQuery?.fallback != null) {
                        flow.chunkedBy { dbQuery.fallback.compareField(it) }.transform { chunk ->
                            emitAll(chunk.sortedWith(dbQuery.fallback.comparator).asFlow())
                        }
                    } else flow
                }
        } else {
            database().transaction(storeName) {
                val dbQuery = query?.let { resolveQuery(it) }
                val store = objectStore(storeName)
                val queryable = dbQuery?.indexName?.let { store.index(dbQuery.indexName) } ?: store
                queryable.getAll(dbQuery?.key)
                    .map { extract(it) }
                    .let { if (dbQuery?.reverse == true) it.asReversed() else it }
                    .let { list ->
                        if (dbQuery?.fallback != null) {
                            list.chunkedBy(dbQuery.fallback.compareField).flatMap { chunk ->
                                chunk.sortedWith(dbQuery.fallback.comparator)
                            }
                        } else list
                    }
            }.asFlow()
        }.map {
            postFetch(it)
        }

    protected open suspend fun postFetch(data: T): T = data

    protected fun <R : Any> Flow<T>.applySelectToFlow(
        field: (T) -> R?,
        comparator: Comparator<R>,
        query: RetrieveQuery.Sort<T, R>
    ) =
        this.listTransform { list ->
            list.selectByField(field, comparator, query)
        }

    protected fun <R : Comparable<R>> Flow<T>.applySelectToFlow(
        field: (T) -> R?,
        query: RetrieveQuery.Sort<T, R>
    ) =
        this.listTransform { list ->
            list.selectByField(field, query)
        }

    protected data class FallbackSort<T, R : Any>(val compareField: (T) -> R?, val comparator: Comparator<T>)

    protected data class DatabaseQuery<T>(
        val indexName: String?,
        val key: Key?,
        val reverse: Boolean = false,
        val fallback: FallbackSort<T, *>?
    ) {
        companion object {
            operator fun <T, R : Any> invoke(
                indexName: String,
                field: ((T) -> R?)?,
                sortQuery: RetrieveQuery.Sort<T, R>,
                typeMapper: ((R) -> dynamic)? = null
            ): DatabaseQuery<T> {
                val from = sortQuery.from?.let { typeMapper?.invoke(it) ?: it }
                val to = sortQuery.to?.let { typeMapper?.invoke(it) ?: it }
                val key = when {
                    from != null && to != null -> bound(from, to)
                    from != null -> lowerBound(from)
                    to != null -> upperBound(to)
                    else -> null
                }
                val fallbackSort = sortQuery.fallbackSort
                val fallback = if (fallbackSort != null && field != null) {
                    FallbackSort(field, fallbackSort)
                } else null
                return DatabaseQuery(indexName, key, !sortQuery.ascending, fallback)
            }

            operator fun <T, R : Any> invoke(
                indexName: String,
                filterQuery: RetrieveQuery.Filter<T, R>,
                typeMapper: ((R) -> dynamic)? = null
            ) = DatabaseQuery<T>(
                indexName,
                Key(filterQuery.target.let { typeMapper?.invoke(it) ?: it }),
                false,
                null
            )
        }
    }
}