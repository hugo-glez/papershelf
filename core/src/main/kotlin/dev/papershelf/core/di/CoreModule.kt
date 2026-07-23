package dev.papershelf.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.papershelf.core.architecture.DefaultDispatcherProvider
import dev.papershelf.core.architecture.DispatcherProvider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreModule {
    @Binds
    @Singleton
    abstract fun bindDispatcherProvider(
        implementation: DefaultDispatcherProvider,
    ): DispatcherProvider
}
