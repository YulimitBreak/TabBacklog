package entity.retrieve

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow

sealed interface RetrieveRequest<T> {

    fun resolve(resolver: RetrieveResolver<T>): Flow<T>

    fun <R> sort(
        field: RetrieveField<T, R>,
        ascending: Boolean = true,
        from: R? = null,
        to: R? = null,
    ): RetrieveRequest<T> = RetrieveBuilder(this).sort(field, ascending, from, to)

    fun list(f: (List<T>) -> List<T>): RetrieveRequest<T> = RetrieveBuilder(this).list(f)

    fun map(f: (T) -> T): RetrieveRequest<T> = RetrieveBuilder(this).map(f)

    fun limit(count: Int): RetrieveRequest<T> = RetrieveBuilder(this).limit(count)

    fun filter(f: (T) -> Boolean): RetrieveRequest<T> = RetrieveBuilder(this).filter(f)

    private data class Single<T>(val builder: RetrieveScope<T>.() -> RetrieveRequest<T>) : RetrieveRequest<T> {
        override fun resolve(resolver: RetrieveResolver<T>): Flow<T> = builder(resolver.scope).resolve(resolver)
    }

    class Fetch<T>() : RetrieveRequest<T> {
        override fun resolve(resolver: RetrieveResolver<T>): Flow<T> = RetrieveBuilder(this).resolve(resolver)

        override fun toString(): String = "Fetch"
    }

    class Empty<T>() : RetrieveRequest<T> {
        override fun resolve(resolver: RetrieveResolver<T>): Flow<T> = emptyFlow()

        override fun toString(): String = "Empty"
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