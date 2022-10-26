package data

import androidx.compose.runtime.compositionLocalOf
import browser.tabs.Tab
import entity.error.CompositionLocalError

interface BrowserInteractor {

    suspend fun getCurrentTab(): Tab
    fun openManager()
    fun openPage(url: String)

    companion object {
        val Local = compositionLocalOf<BrowserInteractor> {
            throw CompositionLocalError("BrowserInteractor")
        }
    }
}