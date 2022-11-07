package entity.retrieve

import kotlinx.coroutines.flow.*

abstract class RetrieveResolver<T, Query : RetrieveQuery<T>> {

    open val scope: RetrieveScope<T, Query> = object : RetrieveScope<T, Query> {}

    tailrec fun resolve(builder: RetrieveBuilder<T, Query>): Flow<T> =
        when (val base = builder.base) {
            is RetrieveRequest.Fetch -> resolveFetch(builder.actions)
            is RetrieveRequest.Join -> resolveJoin(base.requests, builder.actions)
            is RetrieveRequest.Empty -> emptyFlow()
            is RetrieveBuilder -> resolve(RetrieveBuilder(base.base, base.actions + builder.actions))
            is RetrieveRequest.Deferred -> resolve(RetrieveBuilder(base.innerRequest(scope), builder.actions))
        }

    protected abstract fun applySelectToList(source: List<T>, query: Query): List<T>

    protected abstract fun resolveFetch(actions: List<RetrieveBuilder.Action<T, Query>>): Flow<T>


    private fun resolveJoin(
        requests: List<RetrieveRequest<T, Query>>,
        actions: List<RetrieveBuilder.Action<T, Query>>
    ): Flow<T> =
        if (
            actions.any { it is RetrieveBuilder.Action.ListAction } ||
            actions.any { it is RetrieveBuilder.Action.Select && it.query is RetrieveQuery.Sort<*, *> }
        ) {
            // Collect all flow and convert to list before applying breaking changes
            // So sort worked on entire flow instead of applying to each request separately
            flow<T> {
                requests.map { request ->
                    request.resolve(this@RetrieveResolver).toList()
                }.flatten()
                    .let { applyToList(it, actions) }
                    .asFlow().let { emitAll(it) }
            }
        } else {
            flow {
                requests.forEach {
                    emitAll(resolve(RetrieveBuilder(it, actions)))
                }
            }
        }

    protected open fun applyToList(source: List<T>, actions: List<RetrieveBuilder.Action<T, Query>>): List<T> {
        var result = source
        actions.forEach { action ->
            result = when (action) {
                is RetrieveBuilder.Action.Filter -> result.filter { action.criteria(it) }
                is RetrieveBuilder.Action.Limit -> result.take(action.count)
                is RetrieveBuilder.Action.ListAction -> action.transform(result)
                is RetrieveBuilder.Action.Map -> result.map { action.transform(it) }
                is RetrieveBuilder.Action.Select -> applySelectToList(result, action.query)
            }
        }
        return result
    }

    protected fun <R : Comparable<R>> sortListByField(
        source: List<T>,
        field: (T) -> R?,
        query: RetrieveQuery.Sort<T, R>
    ) =
        sortListByField(
            source, field, naturalOrder(), query.ascending, query.from, query.to, query.fallbackSort
        )

    protected fun <R : Any> sortListByField(
        source: List<T>,
        field: (T) -> R?,
        comparator: Comparator<R>,
        query: RetrieveQuery.Sort<T, R>
    ) =
        sortListByField(
            source, field, comparator, query.ascending, query.from, query.to, query.fallbackSort
        )

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun <R> sortListByField(
        source: List<T>,
        field: (T) -> R?,
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