package entity.retrieve

interface RetrieveScope<T, Query : RetrieveQuery<T>> {

    fun fetch(): RetrieveRequest<T, Query> = RetrieveBuilder(RetrieveRequest.Fetch())

    fun join(vararg requests: RetrieveRequest<T, Query>?): RetrieveRequest<T, Query> =
        with(requests.mapNotNull { it }) {
            when (size) {
                0 -> RetrieveRequest.Empty()
                1 -> first()
                else -> RetrieveRequest.Join(this)
            }
        }

    fun empty() = RetrieveRequest.Empty<T, Query>()
}