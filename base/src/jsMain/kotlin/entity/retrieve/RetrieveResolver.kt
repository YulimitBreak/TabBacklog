package entity.retrieve

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow

abstract class RetrieveResolver<T, Query : RetrieveQuery<T>> {

    open val scope: RetrieveScope<T, Query> = object : RetrieveScope<T, Query> {}

    tailrec fun resolve(builder: RetrieveBuilder<T, Query>): Flow<T> =
        when (val base = builder.base) {
            is RetrieveRequest.Fetch -> resolveFetch(builder.actions)
            is RetrieveRequest.Join -> flow {
                // TODO handle breaking operations
                base.requests.forEach {
                    @Suppress("NON_TAIL_RECURSIVE_CALL")
                    emitAll(resolve(RetrieveBuilder(it, actions = builder.actions)))
                }
            }

            is RetrieveRequest.Empty -> emptyFlow()
            is RetrieveBuilder -> resolve(RetrieveBuilder(base.base, base.actions + builder.actions))
            is RetrieveRequest.Deferred -> resolve(RetrieveBuilder(base.innerRequest(scope), builder.actions))
        }

    protected abstract fun resolveFetch(actions: List<RetrieveBuilder.Action<T, Query>>): Flow<T>
}