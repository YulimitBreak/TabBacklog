package entity.retrieve

import kotlinx.coroutines.flow.Flow

data class RetrieveBuilder<T, Query : RetrieveQuery<T>>(
    val base: RetrieveRequest<T, Query>,
    val actions: List<Action<T, Query>> = emptyList()
) :
    RetrieveRequest<T, Query> {

    override fun select(query: Query): RetrieveRequest<T, Query> = copy(actions = actions + Action.Select(query))

    override fun list(f: (List<T>) -> List<T>) = copy(actions = actions + Action.ListAction(f))

    override fun map(f: (T) -> T) = copy(actions = actions + Action.Map(f))

    override fun filter(f: (T) -> Boolean) = copy(actions = actions + Action.Filter(f))

    override suspend fun resolve(resolver: RetrieveResolver<T, Query>): Flow<T> = resolver.resolve(this)

    sealed interface Action<T, Query : RetrieveQuery<T>> {

        data class Select<T, Query : RetrieveQuery<T>>(val query: Query) : Action<T, Query>

        data class ListAction<T, Query : RetrieveQuery<T>>(val transform: (List<T>) -> List<T>) : Action<T, Query>

        data class Map<T, Query : RetrieveQuery<T>>(val transform: (T) -> T) : Action<T, Query>

        data class Filter<T, Query : RetrieveQuery<T>>(val criteria: (T) -> Boolean) : Action<T, Query>
    }
}