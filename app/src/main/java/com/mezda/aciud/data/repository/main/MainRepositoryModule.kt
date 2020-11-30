package com.mezda.aciud.data.repository.main

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
class MainRepositoryModule {

    @Singleton
    @Provides
    fun providesMainRepositoryImpl(): MainRepositoryImpl {
        return MainRepositoryImpl()
    }
}