package entity.retrieve

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

abstract class ListRetrieveResolver<T, Query : RetrieveQuery<T>>(val source: List<T>) : RetrieveResolver<T, Query>() {

    abstract fun handleSelect(source: List<T>, action: RetrieveBuilder.Action.Select<T, Query>): List<T>

    override fun resolveFetch(actions: List<RetrieveBuilder.Action<T, Query>>): Flow<T> {
        var result = source
        actions.forEach { action ->
            result = when (action) {
                is RetrieveBuilder.Action.Filter -> result.filter { action.criteria(it) }
                is RetrieveBuilder.Action.Limit -> result.take(action.count)
                is RetrieveBuilder.Action.ListAction -> action.transform(result)
                is RetrieveBuilder.Action.Map -> result.map { action.transform(it) }
                is RetrieveBuilder.Action.Select -> handleSelect(result, action)
            }
        }
        return result.asFlow()
    }
}