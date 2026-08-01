package com.deacx.widget.di

import com.deacx.widget.data.repository.PreferencesRepositoryImpl
import com.deacx.widget.domain.repository.PreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds the domain-facing PreferencesRepository interface to its DataStore-
 * backed implementation, so ViewModels and (later) the widget's update
 * logic depend on the abstraction, not the storage mechanism.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(
        impl: PreferencesRepositoryImpl
    ): PreferencesRepository
}
