package entity.retrieve

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow

sealed interface RetrieveRequest<T> {

    fun resolve(resolver: RetrieveResolver<T>): Flow<T>

    private data class Single<T>(val builder: RetrieveScope<T>.() -> RetrieveRequest<T>) : RetrieveRequest<T> {
        override fun resolve(resolver: RetrieveResolver<T>): Flow<T> = builder(resolver.scope).resolve(resolver)
    }

    class Empty<T>() : RetrieveRequest<T> {
        override fun resolve(resolver: RetrieveResolver<T>): Flow<T> = emptyFlow()
    }

    data class Join<T>(val requests: List<RetrieveRequest<T>>) : RetrieveRequest<T> {
        override fun resolve(resolver: RetrieveResolver<T>): Flow<T> = flow {
            requests.forEach {
                emitAll(it.resolve(resolver))
            }
        }
    }

    companion object {
        operator fun <T> invoke(builder: RetrieveScope<T>.() -> RetrieveRequest<T>): RetrieveRequest<T> =
            Single(builder)
    }
}