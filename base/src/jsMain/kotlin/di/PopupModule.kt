package di

import dev.shustoff.dikt.Create
import dev.shustoff.dikt.UseModules
import kotlinx.coroutines.CoroutineScope
import ui.popup.PopupModel

@Suppress("unused")
@UseModules(AppModule::class)
class PopupModule(
    val appModule: AppModule,
    val scope: CoroutineScope,
) {

    val model by lazy {
        provideModel()
    }

    @Create
    private fun provideModel(): PopupModel
}