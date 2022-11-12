package entity.retrieve

import common.listTransform
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

abstract class ListRetrieveResolver<T, Query : RetrieveQuery<T>>(val source: List<T>) : RetrieveResolver<T, Query>() {

    abstract fun applyQueryToList(list: List<T>, query: Query): List<T>
    final override fun fetchFlow(query: Query?, hasReorderingActions: Boolean): Flow<T> =
        (query?.let { applyQueryToList(source, query) } ?: source).asFlow()

    final override fun applyQueryToFlow(flow: Flow<T>, query: Query): Flow<T> =
        flow.listTransform { applyQueryToList(it, query) }
}