package entity.retrieve

import kotlinx.coroutines.flow.Flow

data class RetrieveBuilder<T>(val actions: List<Action<T>>) : RetrieveRequest<T> {

    fun <R> sort(
        field: RetrieveField<T, R>,
        ascending: Boolean = true,
        from: R? = null,
        to: R? = null,
    ) = copy(actions = actions + Action.Sort(field, ascending, from, to))

    fun list(f: (List<T>) -> List<T>) = copy(actions = actions + Action.ListAction(f))

    fun map(f: (T) -> T) = copy(actions = actions + Action.Map(f))

    fun limit(count: Int) = copy(actions = actions + Action.Limit(count))

    fun filter(f: (T) -> Boolean) = copy(actions = actions + Action.Filter(f))

    override fun resolve(resolver: RetrieveResolver<T>): Flow<T> = resolver.resolve(this)

    sealed interface Action<T> {
        data class Sort<T, R>(
            val field: RetrieveField<T, R>,
            val ascending: Boolean = true,
            val from: R? = null,
            val to: R? = null,
        ) : Action<T>

        data class ListAction<T>(val f: (List<T>) -> List<T>) : Action<T>

        data class Map<T>(val f: (T) -> T) : Action<T>

        data class Limit<T>(val count: Int) : Action<T>

        data class Filter<T>(val f: (T) -> Boolean) : Action<T>
    }
}