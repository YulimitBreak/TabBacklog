package entity.retrieve

import common.listTransform
import entity.retrieve.RetrieveResolver.FlowSource
import kotlinx.coroutines.flow.*

abstract class RetrieveResolver<T, Query : RetrieveQuery<T>> {

    open val scope: RetrieveScope<T, Query> = object : RetrieveScope<T, Query> {}

    tailrec fun resolve(builder: RetrieveBuilder<T, Query>): Flow<T> = when (val base = builder.base) {
        is RetrieveRequest.Fetch -> resolveFlow(::fetchFlow, builder.actions)
        is RetrieveRequest.Join -> resolveFlow(joinFlowSource(base.requests), builder.actions)
        is RetrieveRequest.Empty -> emptyFlow()
        is RetrieveBuilder -> resolve(RetrieveBuilder(base.base, base.actions + builder.actions))
        is RetrieveRequest.Deferred -> resolve(RetrieveBuilder(base.innerRequest(scope), builder.actions))
    }


    abstract fun fetchFlow(query: Query?, hasReorderingActions: Boolean): Flow<T>
    abstract fun applyQueryToFlow(flow: Flow<T>, query: Query): Flow<T>

    private fun resolveFlow(source: FlowSource<T, Query>, actions: List<RetrieveBuilder.Action<T, Query>>): Flow<T> {
        val firstQuery = actions.filterIsInstance<RetrieveBuilder.Action.Select<T, Query>>().firstOrNull()
        val flow: Flow<T>
        val postFlowActions: List<RetrieveBuilder.Action<T, Query>>

        fun List<RetrieveBuilder.Action<T, Query>>.hasReorderingActions() = any {
            (it is RetrieveBuilder.Action.Select && it.query is RetrieveQuery.Sort<*, *>) ||
                    it is RetrieveBuilder.Action.ListAction
        }

        val preQueryActions = firstQuery?.let { actions.take(actions.indexOf(it)) }
        when {
            // If there's no first query and pre-query actions, just execute flow without query and any reordering
            preQueryActions == null -> {
                postFlowActions = actions
                flow = source(null, postFlowActions.hasReorderingActions())
            }
            // If actions start with query, no problems at all, just pass into source and execute the rest
            preQueryActions.isEmpty() -> {
                postFlowActions = actions - firstQuery
                flow = source(firstQuery.query, postFlowActions.hasReorderingActions())
            }
            // If all actions are "safe" - no reordering, no changing data, they can be safely moved after the query
            preQueryActions.all {
                it is RetrieveBuilder.Action.Filter
            } -> {
                postFlowActions = preQueryActions + (actions - firstQuery)
                flow = source(firstQuery.query, postFlowActions.hasReorderingActions())
            }
            // Otherwise we have breaking operations before query, so we just treat query as a normal operation
            else -> {
                postFlowActions = actions
                flow = source(null, postFlowActions.hasReorderingActions())
            }
        }

        return postFlowActions.fold(flow) { result, action ->
            when (action) {
                is RetrieveBuilder.Action.Filter -> result.filter(action.criteria)
                is RetrieveBuilder.Action.Map -> result.map(action.transform)
                is RetrieveBuilder.Action.ListAction -> result.listTransform(action.transform)
                is RetrieveBuilder.Action.Select -> applyQueryToFlow(result, action.query)
            }
        }
    }

    private fun joinFlowSource(requests: List<RetrieveRequest<T, Query>>) =
        FlowSource<T, Query> { query, _ ->
            when (query) {
                null -> flow {
                    requests.forEach { request ->
                        emitAll(
                            request.resolve(this@RetrieveResolver)
                        )
                    }
                }

                is RetrieveQuery.Sort<*, *> -> flow {
                    requests.forEach { request ->
                        emitAll(request.resolve(this@RetrieveResolver))
                    }
                }.let { applyQueryToFlow(it, query) }

                else -> flow {
                    requests.forEach { request ->
                        emitAll(request.resolve(this@RetrieveResolver))
                    }
                }
            }
        }

    private fun interface FlowSource<T, Query : RetrieveQuery<T>> {
        operator fun invoke(query: Query?, hasReorderingActions: Boolean): Flow<T>
    }


}