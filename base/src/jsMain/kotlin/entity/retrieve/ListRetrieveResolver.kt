package entity.retrieve

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

abstract class ListRetrieveResolver<T>(val source: List<T>) : RetrieveResolver<T>() {

    abstract fun handleSort(source: List<T>, action: RetrieveBuilder.Action.Sort<T, *>): List<T>

    override fun resolveFetch(actions: List<RetrieveBuilder.Action<T>>): Flow<T> {
        var result = source
        actions.forEach { action ->
            when (action) {
                is RetrieveBuilder.Action.Filter -> result = result.filter { action.criteria(it) }
                is RetrieveBuilder.Action.Limit -> result = result.take(action.count)
                is RetrieveBuilder.Action.ListAction -> result = action.transform(result)
                is RetrieveBuilder.Action.Map -> result = result.map { action.transform(it) }
                is RetrieveBuilder.Action.Sort<T, *> -> result = handleSort(result, action)
            }
        }
        return result.asFlow()
    }
}