package entity.retrieve

interface RetrieveScope<T> {

    fun request(vararg requests: RetrieveRequest<T>): RetrieveRequest<T> = when (requests.size) {
        0 -> RetrieveRequest.Empty()
        1 -> requests.first()
        else -> RetrieveRequest.Join(requests.toList())
    }

    fun empty() = RetrieveRequest.Empty<T>()
}