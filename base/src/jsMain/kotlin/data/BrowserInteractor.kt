package data

import androidx.compose.runtime.compositionLocalOf
import browser.tabs.Tab
import entity.error.CompositionLocalError
import kotlinx.coroutines.flow.Flow

interface BrowserInteractor {

    suspend fun getCurrentTab(): Tab
    fun openManager()
    fun openPage(url: String)

    suspend fun sendUpdateMessage(url: String)

    fun subscribeToDbUpdates(): Flow<String>

    companion object {
        val Local = compositionLocalOf<BrowserInteractor> {
            throw CompositionLocalError("BrowserInteractor")
        }
    }
}