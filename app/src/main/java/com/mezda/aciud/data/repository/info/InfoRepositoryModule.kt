package com.mezda.aciud.data.repository.info

import com.mezda.aciud.data.api.ApiCalls
import com.mezda.aciud.data.preference.Preference
import com.mezda.aciud.data.repository.register.RegisterRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
class InfoRepositoryModule {

    @ActivityScoped
    @Provides
    fun providesInfoRepositoryRepository(apiCalls: ApiCalls, preference: Preference): InfoRepositoryImpl {
        return InfoRepositoryImpl(apiCalls, preference)
    }
}