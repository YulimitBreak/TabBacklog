package entity.retrieve

interface RetrieveScope<T> {

    fun fetch(): RetrieveRequest<T> = RetrieveBuilder(RetrieveRequest.Fetch())

    fun join(vararg requests: RetrieveRequest<T>?): RetrieveRequest<T> = with(requests.mapNotNull { it }) {
        when (size) {
            0 -> RetrieveRequest.Empty()
            1 -> first()
            else -> RetrieveRequest.Join(this)
        }
    }

    fun empty() = RetrieveRequest.Empty<T>()
}