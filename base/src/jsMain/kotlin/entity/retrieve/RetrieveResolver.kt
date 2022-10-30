package entity.retrieve

import kotlinx.coroutines.flow.Flow

interface RetrieveResolver<T> {

    val scope: RetrieveScope<T>

    fun resolve(builder: RetrieveBuilder<T>): Flow<T>
}