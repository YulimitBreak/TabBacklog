package di

import androidx.compose.runtime.staticCompositionLocalOf

object ModuleLocal {
    val App = staticCompositionLocalOf { AppModule(RepositoryModule()) }
}