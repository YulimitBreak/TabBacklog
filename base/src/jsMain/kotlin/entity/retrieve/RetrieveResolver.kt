package entity.retrieve

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow

abstract class RetrieveResolver<T> {

    open val scope: RetrieveScope<T> = object : RetrieveScope<T> {}

    tailrec fun resolve(builder: RetrieveBuilder<T>): Flow<T> =
        when (val base = builder.base) {
            is RetrieveRequest.Fetch -> TODO()
            is RetrieveRequest.Join -> flow {
                base.requests.forEach {
                    @Suppress("NON_TAIL_RECURSIVE_CALL")
                    emitAll(resolve(RetrieveBuilder(it, actions = builder.actions)))
                }
            }

            is RetrieveRequest.Empty -> emptyFlow()
            is RetrieveBuilder -> resolve(RetrieveBuilder(base.base, base.actions + builder.actions))
            is RetrieveRequest.Deferred -> resolve(RetrieveBuilder(base.innerRequest(scope), builder.actions))
        }

    abstract fun resolveFetch(actions: List<RetrieveBuilder.Action<T>>): Flow<T>
}