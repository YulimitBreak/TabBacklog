package di

import dev.shustoff.dikt.CreateSingle
import dev.shustoff.dikt.UseModules
import kotlinx.coroutines.CoroutineScope
import ui.popup.PopupModel

@UseModules(AppModule::class)
class PopupModule(
    val appModule: AppModule,
    val scope: CoroutineScope,
) {

    @CreateSingle
    fun provideModel(): PopupModel
}