package entity.retrieve

import common.comparator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

abstract class ListRetrieveResolver<T, Query : RetrieveQuery<T>>(val source: List<T>) : RetrieveResolver<T, Query>() {

    abstract fun handleSelect(source: List<T>, query: Query): List<T>

    override fun resolveFetch(actions: List<RetrieveBuilder.Action<T, Query>>): Flow<T> {
        var result = source
        actions.forEach { action ->
            result = when (action) {
                is RetrieveBuilder.Action.Filter -> result.filter { action.criteria(it) }
                is RetrieveBuilder.Action.Limit -> result.take(action.count)
                is RetrieveBuilder.Action.ListAction -> action.transform(result)
                is RetrieveBuilder.Action.Map -> result.map { action.transform(it) }
                is RetrieveBuilder.Action.Select -> handleSelect(result, action.query)
            }
        }
        return result.asFlow()
    }


    protected fun <R : Comparable<R>> sortByField(
        source: List<T>,
        field: (T) -> R?,
        query: RetrieveQuery.Sort<T, R>
    ) =
        sortByField(
            source, field, comparator(), query.ascending, query.from, query.to
        )

    protected fun <R : Any> sortByField(
        source: List<T>,
        field: (T) -> R?,
        comparator: Comparator<R>,
        query: RetrieveQuery.Sort<T, R>
    ) =
        sortByField(
            source, field, comparator, query.ascending, query.from, query.to
        )

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun <R> sortByField(
        source: List<T>, field: (T) -> R?,
        comparator: Comparator<R>,
        ascending: Boolean,
        from: R?,
        to: R?
    ): List<T> {
        val sorted = source.filter { field(it) != null }
            .sortedWith { a, b ->
                comparator.compare(field(a)!!, field(b)!!).let {
                    if (ascending) it else -it
                }
            }
        val indexFrom = from?.let { source.indexOfFirst { field(it) == from } } ?: 0
        val indexTo = to?.let { source.indexOfFirst { field(it) == from } } ?: source.lastIndex
        if (indexFrom == -1 || indexTo == -1) return emptyList()
        return sorted.subList(indexFrom, indexTo + 1)
    }
}