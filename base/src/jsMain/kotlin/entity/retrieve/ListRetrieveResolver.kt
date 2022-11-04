package entity.retrieve

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
            source, field, naturalOrder(), query.ascending, query.from, query.to, query.fallbackSort
        )

    protected fun <R : Any> sortByField(
        source: List<T>,
        field: (T) -> R?,
        comparator: Comparator<R>,
        query: RetrieveQuery.Sort<T, R>
    ) =
        sortByField(
            source, field, comparator, query.ascending, query.from, query.to, query.fallbackSort
        )

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun <R> sortByField(
        source: List<T>, field: (T) -> R?,
        comparator: Comparator<R>,
        ascending: Boolean,
        from: R?,
        to: R?,
        fallbackSort: Comparator<T>?,
    ): List<T> {
        var result = source.filter { field(it) != null }
            .sortedWith { a, b ->
                val compareValues =
                    (if (ascending) comparator else comparator.reversed()).compare(field(a)!!, field(b)!!)
                if (compareValues == 0) fallbackSort?.compare(a, b) ?: compareValues else compareValues
            }
        if (from != null) {
            result = result.dropWhile { comparator.compare(field(it)!!, from) < 0 }
        }
        if (to != null) {
            result = result.dropLastWhile { comparator.compare(field(it)!!, to) > 0 }
        }
        return result
    }
}