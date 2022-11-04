package entity.retrieve

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow

sealed interface RetrieveRequest<T, Query : RetrieveQuery<T>> {

    fun resolve(resolver: RetrieveResolver<T, Query>): Flow<T>

    fun select(query: Query): RetrieveRequest<T, Query> = RetrieveBuilder(this).select(query)

    fun list(f: (List<T>) -> List<T>): RetrieveRequest<T, Query> = RetrieveBuilder(this).list(f)

    fun map(f: (T) -> T): RetrieveRequest<T, Query> = RetrieveBuilder(this).map(f)

    fun limit(count: Int): RetrieveRequest<T, Query> = RetrieveBuilder(this).limit(count)

    fun filter(f: (T) -> Boolean): RetrieveRequest<T, Query> = RetrieveBuilder(this).filter(f)

    data class Deferred<T, Query : RetrieveQuery<T>>(val builder: RetrieveScope<T, Query>.() -> RetrieveRequest<T, Query>) :
        RetrieveRequest<T, Query> {

        fun innerRequest(scope: RetrieveScope<T, Query>) = builder(scope)
        override fun resolve(resolver: RetrieveResolver<T, Query>): Flow<T> =
            innerRequest(resolver.scope).resolve(resolver)
    }

    class Fetch<T, Query : RetrieveQuery<T>>() : RetrieveRequest<T, Query> {
        override fun resolve(resolver: RetrieveResolver<T, Query>): Flow<T> = RetrieveBuilder(this).resolve(resolver)

        override fun toString(): String = "Fetch"
    }

    class Empty<T, Query : RetrieveQuery<T>>() : RetrieveRequest<T, Query> {
        override fun resolve(resolver: RetrieveResolver<T, Query>): Flow<T> = emptyFlow()

        override fun toString(): String = "Empty"
    }

    data class Join<T, Query : RetrieveQuery<T>>(val requests: List<RetrieveRequest<T, Query>>) :
        RetrieveRequest<T, Query> {
        override fun resolve(resolver: RetrieveResolver<T, Query>): Flow<T> = flow {
            requests.forEach {
                emitAll(it.resolve(resolver))
            }
        }
    }

    companion object {
        operator fun <T, Query : RetrieveQuery<T>> invoke(builder: RetrieveScope<T, Query>.() -> RetrieveRequest<T, Query>): RetrieveRequest<T, Query> =
            Deferred(builder)
    }
}