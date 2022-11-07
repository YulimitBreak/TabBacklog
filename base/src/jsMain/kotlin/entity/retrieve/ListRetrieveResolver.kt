package entity.retrieve

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

abstract class ListRetrieveResolver<T, Query : RetrieveQuery<T>>(val source: List<T>) : RetrieveResolver<T, Query>() {

    override fun resolveFetch(actions: List<RetrieveBuilder.Action<T, Query>>): Flow<T> =
        applyToList(source, actions).asFlow()
}