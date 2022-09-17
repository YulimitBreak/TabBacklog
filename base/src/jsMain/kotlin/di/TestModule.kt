package di

import dev.shustoff.dikt.Create
import dev.shustoff.dikt.CreateSingle
import dev.shustoff.dikt.ProvideSingle
import dev.shustoff.dikt.UseModules

@UseModules(ExternalModule::class)
class TestModule(val externalModule: ExternalModule) {

    @CreateSingle
    fun provideTarget(): Target

    @ProvideSingle
    fun provideSource(): Source

}

interface ExternalModule {
    fun provideSource(): Source
}

@Suppress("RedundantModalityModifier")
class ExternalModuleImplementation() : ExternalModule {

    private val number: Int = 10

    @Create
    final override fun provideSource(): Source
}

class ExternalModuleImplementation2() : ExternalModule {

    private val number: Int = 20

    @Create
    final override fun provideSource(): Source
}

data class Target(val source: Source)

class Source(val number: Int)